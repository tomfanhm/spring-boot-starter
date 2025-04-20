package com.tomfanhm.security.jwt.security;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
	private static final Pattern IP_PATTERN = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
	private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

	private LoadingCache<String, Bucket> buckets;

	@Value("${rate.limit.cache.expire.hours:1}")
	private int cacheExpireHours;

	@Value("${rate.limit.cache.max.size:10000}")
	private int cacheMaxSize;

	@Value("${rate.limit.capacity:100}")
	private int capacity;

	@Value("${rate.limit.refill.amount:100}")
	private int refillAmount;

	@Value("${rate.limit.refill.duration:1}")
	private int refillDurationMinutes;

	@Value("${rate.limit.trusted.proxies:}")
	private String[] trustedProxies;

	private Bucket createBucket() {
		Refill refill = Refill.greedy(refillAmount, Duration.ofMinutes(refillDurationMinutes));
		Bandwidth limit = Bandwidth.classic(capacity, refill);
		return Bucket.builder().addLimit(limit).build();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		String path = req.getRequestURI();
		if (shouldSkipRateLimiting(path)) {
			chain.doFilter(req, res);
			return;
		}

		String ip = getClientIP(req);

		if (ip == null) {
			logger.warn("Request with invalid IP format, skipping rate limiting.");
			chain.doFilter(req, res);
			return;
		}

		int tokens = getTokensForRequest(req);

		Bucket bucket = buckets.get(ip);
		if (bucket.tryConsume(tokens)) {
			chain.doFilter(req, res);
		} else {
			logger.warn("Rate limit exceeded for IP: {}, path: {}", ip, path);
			res.setStatus(429);
			res.setHeader("Retry-After", String.valueOf(refillDurationMinutes * 60));
			res.getWriter().write("Too many requests. Please try again later.");
		}
	}

	private String getClientIP(HttpServletRequest req) {
		String ip = null;

		String xf = req.getHeader("X-Forwarded-For");
		String remoteAddr = req.getRemoteAddr();

		if (xf != null && isTrustedProxy(remoteAddr)) {
			String clientIP = xf.split(",")[0].trim();
			if (isValidIpFormat(clientIP)) {
				ip = clientIP;
			}
		}

		if (ip == null && isValidIpFormat(remoteAddr)) {
			ip = remoteAddr;
		}

		return ip;
	}

	private int getTokensForRequest(HttpServletRequest req) {
		String method = req.getMethod();
		String path = req.getRequestURI();

		if ("POST".equals(method) || "PUT".equals(method)) {
			return 3;
		}

		if (path.contains("/api/expensive-operation")) {
			return 5;
		}

		return 1;
	}

	@jakarta.annotation.PostConstruct
	public void init() {
		buckets = Caffeine.newBuilder().maximumSize(cacheMaxSize).expireAfterAccess(Duration.ofHours(cacheExpireHours))
				.build(key -> createBucket());

		logger.info("Rate limiting initialized: {} requests per {} minutes, max cache size: {}", capacity,
				refillDurationMinutes, cacheMaxSize);
	}

	private boolean isTrustedProxy(String ipAddress) {
		if (trustedProxies == null || trustedProxies.length == 0) {
			return false;
		}

		for (String proxy : trustedProxies) {
			if (proxy.equals(ipAddress)) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidIpFormat(String ip) {
		return ip != null && IP_PATTERN.matcher(ip).matches();
	}

	private boolean shouldSkipRateLimiting(String path) {
		return path.startsWith("/actuator/health") || path.startsWith("/internal/");
	}
}
