package org.quartz.console.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * quartz数据源配置
 * @author yongdagan@gmail.com
 * @date 2018年6月5日 上午7:50:13
 *
 */
@Configuration
@MapperScan(basePackages = "org.quartz.console.mapper", sqlSessionFactoryRef = "quartzSqlSessionFactory")
public class QuartzDataSourceConfig {
	
	@Autowired
	private QuartzDataSourceProperties properties;
	
	@Bean(name="quartzDataSource")
	public DataSource quartzDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(properties.getDriverClassName());
		dataSource.setUrl(properties.getUrl());
		dataSource.setUsername(properties.getUsername());
		dataSource.setPassword(properties.getPassword());
		dataSource.setInitialSize(properties.getInitialSize());
		dataSource.setMinIdle(properties.getMinIdle());
		dataSource.setMaxActive(properties.getMaxActive());
		dataSource.setMaxWait(properties.getMaxWait());
		dataSource.setValidationQuery("SELECT 'x'");
		return dataSource;
	}
	
	@Bean(name="quartzTransactionManager")
	public DataSourceTransactionManager quartzTransactionManager(
			@Qualifier("quartzDataSource") DataSource quartzDataSource) {
		return new DataSourceTransactionManager(quartzDataSource);
	}
	
	@Bean(name="quartzSqlSessionFactory")
	public SqlSessionFactory quartzSqlSessionFactory(
			@Qualifier("quartzDataSource") DataSource quartzDataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(quartzDataSource);
//		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
//				.getResources("classpath:mapper/*.xml")); TODO
		return sqlSessionFactoryBean.getObject();
	}
	
}
