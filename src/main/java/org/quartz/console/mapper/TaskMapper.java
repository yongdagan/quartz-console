package org.quartz.console.mapper;

import java.util.List;
import java.util.Map;

import org.quartz.console.bean.Task;

public interface TaskMapper {
	
	List<Task> selectList(Map<String, Object> params);
	
	int insert(Task task);
	
	int updateSelective(Task task);
	
	int delete(Task task);
	
	int refresh(Task task);
	
}
