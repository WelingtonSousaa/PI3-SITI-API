package com.siti.sitiapi.configs;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class MySQLMetadataDataSourceProxy {

    public static DataSource wrap(DataSource original) {
        return (DataSource) Proxy.newProxyInstance(
                DataSource.class.getClassLoader(),
                new Class<?>[]{DataSource.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = method.invoke(original, args);
                        if (result instanceof Connection) {
                            return wrapConnection((Connection) result);
                        }
                        return result;
                    }
                }
        );
    }

    private static Connection wrapConnection(Connection original) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getMetaData".equals(method.getName())) {
                            DatabaseMetaData originalMetaData = (DatabaseMetaData) method.invoke(original, args);
                            return wrapMetaData(originalMetaData);
                        }
                        return method.invoke(original, args);
                    }
                }
        );
    }

    private static DatabaseMetaData wrapMetaData(DatabaseMetaData original) {
        return (DatabaseMetaData) Proxy.newProxyInstance(
                DatabaseMetaData.class.getClassLoader(),
                new Class<?>[]{DatabaseMetaData.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getDatabaseProductName".equals(method.getName())) {
                            return "MySQL";
                        }
                        if ("getDatabaseProductVersion".equals(method.getName())) {
                            return "8.0";
                        }
                        if ("getProcedureColumns".equals(method.getName())) {
                            ResultSet originalRs = (ResultSet) method.invoke(original, args);
                            return wrapResultSet(originalRs);
                        }
                        return method.invoke(original, args);
                    }
                }
        );
    }

    private static ResultSet wrapResultSet(ResultSet original) {
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getString".equals(method.getName()) || "getObject".equals(method.getName())) {
                            Object column = args[0];
                            boolean isColumnNameQuery = false;
                            if (column instanceof String && "COLUMN_NAME".equalsIgnoreCase((String) column)) {
                                isColumnNameQuery = true;
                            } else if (column instanceof Integer && (Integer) column == 4) {
                                isColumnNameQuery = true;
                            }

                            if (isColumnNameQuery) {
                                String procedureName = original.getString("PROCEDURE_NAME");
                                int position = original.getInt("ORDINAL_POSITION");
                                String mappedName = mapParameterName(procedureName, position);
                                if (mappedName != null) {
                                    return mappedName;
                                }
                            }
                        }
                        return method.invoke(original, args);
                    }
                }
        );
    }

    private static String mapParameterName(String procedureName, int position) {
        if (procedureName == null) return null;
        String name = procedureName.toUpperCase();
        if (name.contains("PROCCREATEUSER")) {
            if (position == 1) return "p_email";
            if (position == 2) return "p_password";
            if (position == 3) return "p_identifier_document";
            if (position == 4) return "p_name";
        } else if (name.contains("PROCCREATEPASSENGER")) {
            if (position == 1) return "p_id";
            if (position == 2) return "p_birth_date";
            if (position == 3) return "p_phone";
            if (position == 4) return "p_type";
            if (position == 5) return "p_registration_number";
            if (position == 6) return "p_bond_proof";
            if (position == 7) return "p_id_address";
        } else if (name.contains("PROCCREATEDRIVER")) {
            if (position == 1) return "p_id";
            if (position == 2) return "p_cnh_number";
            if (position == 3) return "p_cnh_category";
            if (position == 4) return "p_name";
            if (position == 5) return "p_birth_date";
            if (position == 6) return "p_cnh_validity_date";
            if (position == 7) return "p_phone";
            if (position == 8) return "p_id_address";
        } else if (name.contains("PROCGETUSERBYEMAILANDPASSWORD")) {
            if (position == 1) return "p_email";
            if (position == 2) return "p_password";
        } else if (name.contains("PROCGETUSERBYEMAIL")) {
            if (position == 1) return "p_email";
        } else if (name.contains("PROCEXISTUSERBYEMAIL")) {
            if (position == 1) return "p_email";
        } else if (name.contains("HASUSERADMINISTRATORBYID")) {
            if (position == 1) return "p_id";
        } else if (name.contains("HASUSERDRIVERBYID")) {
            if (position == 1) return "p_id";
        }
        return null;
    }
}
