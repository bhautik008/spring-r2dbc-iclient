package org.reactive.r2dbc.iclient.mapper;

import static org.springframework.util.Assert.notNull;

import org.reactive.r2dbc.iclient.support.R2dbcSqlDaoSupport;
import org.springframework.beans.factory.FactoryBean;

/**
 * BeanFactory that enables injection of R2dbc iClient mapper interfaces.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcMapperBeanFactory<T> extends R2dbcSqlDaoSupport implements FactoryBean<T> {

	private Class<?> mapperInterface;

	public R2dbcMapperBeanFactory() {
	}

	public R2dbcMapperBeanFactory(Class<?> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) this.getR2dbcSqlSession().getMapper(mapperInterface);
	}

	@Override
	public Class<?> getObjectType() {
		return mapperInterface;
	}

	@Override
	protected void checkDaoConfig() {
		super.checkDaoConfig();
		notNull(this.mapperInterface, "Property 'mapperInterface' is required");
	}

	public Class<?> getMapperInterface() {
		return mapperInterface;
	}

	public void setMapperInterface(Class<?> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}
}
