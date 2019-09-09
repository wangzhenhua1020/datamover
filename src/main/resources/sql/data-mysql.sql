-- 测试数据
INSERT INTO `data_mover_datasource` VALUES (1, '北建接口库', 'mppdb', 'jdbc:postgresql://59.195.6.189:25308/adq_gldm', 'org.postgresql.Driver', 'adqbj', 'Sxbj@5tgb', NULL, 0, '2019-08-19 09:43:47', '2019-08-19 09:43:47', NULL);
INSERT INTO `data_mover_datasource` VALUES (2, '睿呈展现库', 'mppdb', 'jdbc:postgresql://59.195.6.189:25308/adq_gldm', 'org.postgresql.Driver', 'adqrc', 'Sxrc@5tgb', NULL, 0, '2019-08-19 09:45:18', '2019-08-19 09:45:18', NULL);
INSERT INTO `data_mover_datasource` VALUES (3, 'mysql_127.0.0.1', 'mysql', 'jdbc:mysql://127.0.0.1:3306/swc?useUnicode=true&characterEncoding=UTF-8', 'com.mysql.jdbc.Driver', 'root', 'root', NULL, 0, '2019-08-17 21:14:00', '2019-08-17 21:14:00', NULL);
INSERT INTO `data_mover_datasource` VALUES (4, 'postgresql_127.0.0.1', 'postgres', 'jdbc:postgresql://127.0.0.1:5432/postgres', 'org.postgresql.Driver', 'postgres', 'postgres', NULL, 0, '2019-08-17 21:15:00', '2019-08-17 21:15:00', NULL);

INSERT INTO `data_mover_config` VALUES (1, '测试迁移配置1', 3, 0, 'select * from t_enterprise_info', 4, 'dst.t_enterprise_info', 1, '[\"id\"]', 120, 1, 1, 0, 0, NULL, '2019-08-14 18:02:38', '2019-08-14 18:02:38', NULL);
INSERT INTO `data_mover_config` VALUES (2, '测试迁移配置2', 4, 1, 'INSERT INTO adqbj.bj_hw_car_run_info  select * from adqbj.bj_hw_car_run_info', NULL, NULL, NULL, NULL, NULL, 1, 1, 0, 0, NULL, '2019-08-14 18:02:38', '2019-08-14 18:02:38', NULL);

INSERT INTO `data_mover_schedule` VALUES (1, '调度测试', 1, '0 0/5 * * * ?', 0, '2019-08-17 21:14:00', '2019-08-17 21:14:00', NULL);

COMMIT;
