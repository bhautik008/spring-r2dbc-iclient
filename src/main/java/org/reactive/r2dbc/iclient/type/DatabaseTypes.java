package org.reactive.r2dbc.iclient.type;

public enum DatabaseTypes {

	MySQL("mysql"), MSSQL("mssql"), Oracle("oracle"), Postgresql("postgresql"), MariaDB("mariadb");

	private String databaseType;

	private DatabaseTypes(String type) {
		this.databaseType = type;
	}

	public String getDatabaseType() {
		return this.databaseType;
	}

	public static DatabaseTypes getDatabaseType(String type) {
		for (DatabaseTypes types : DatabaseTypes.values()) {
			if (types.getDatabaseType().equals(type)) {
				return types;
			}
		}
		return MySQL;
	}
}
