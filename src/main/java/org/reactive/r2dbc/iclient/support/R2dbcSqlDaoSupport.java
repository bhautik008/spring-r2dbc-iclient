package org.reactive.r2dbc.iclient.support;

import static org.springframework.util.Assert.notNull;

import org.reactive.r2dbc.iclient.core.R2dbcSqlSession;
import org.springframework.dao.support.DaoSupport;

/**
 * Convenient super class for R2dbcSqlSession data access objects. 
 * <p>
 * This class needs a R2dbcSqlSessionFactory.
 * <p>
 * 
 * @author Bhautik Bhanani
 */
public abstract class R2dbcSqlDaoSupport extends DaoSupport {

	private R2dbcSqlSession r2dbcSqlSession;

	public R2dbcSqlSession getR2dbcSqlSession() {
		return r2dbcSqlSession;
	}

	public void setR2dbcSqlSession(R2dbcSqlSession r2dbcSqlSession) {
		this.r2dbcSqlSession = r2dbcSqlSession;
	}

	@Override
	protected void checkDaoConfig() {
		notNull(this.r2dbcSqlSession, "Property 'r2dbcSqlSession' is required");
	}

}
