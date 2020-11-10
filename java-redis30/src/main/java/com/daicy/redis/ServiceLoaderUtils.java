/*
 * Copyright 2014-2016 xcar.com All right reserved.
 */
package com.daicy.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author daichangya
 */
public class ServiceLoaderUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceLoaderUtils.class);

	private static final ConcurrentMap<Class<?>, Object> _service_holder = new ConcurrentHashMap<>();

	private static final ConcurrentMap<Class<?>, ServiceLoader<?>> _service_loader_holder = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T loadService(Class<T> svc) {
		if (svc == null) {
			throw new IllegalArgumentException("svc should not be null");
		}

		if (_service_holder.containsKey(svc)) {
			return (T) _service_holder.get(svc);
		}

		ServiceLoader<T> serviceLoader = (ServiceLoader<T>) _service_loader_holder.get(svc);
		if (serviceLoader == null) {
			serviceLoader = ServiceLoader.load(svc);
			_service_loader_holder.putIfAbsent(svc, serviceLoader);
		}

		T service = serviceLoader.iterator().next();
		if (service != null){
			_service_holder.putIfAbsent(svc, service);
		}

		return service;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> loadServices(Class<T> svc) {
		if (svc == null) {
			LOG.error("svc should not be null");
			throw new IllegalArgumentException("svc should not be null");
		}
		try {
			if (_service_holder.containsKey(svc)) {
				return (List<T>) _service_holder.get(svc);
			}

			ServiceLoader<T> serviceLoader = (ServiceLoader<T>) _service_loader_holder.get(svc);
			if (serviceLoader == null) {
				serviceLoader = ServiceLoader.load(svc);
				_service_loader_holder.putIfAbsent(svc, serviceLoader);
			}

			List<T> services = new ArrayList<>();
			for (Iterator<T> iterator = serviceLoader.iterator(); iterator.hasNext();) {
				services.add(iterator.next());
			}
			return services;
		}catch (Exception e){
			LOG.error("loadServices error ",e);
		}
		return null;
	}
}
