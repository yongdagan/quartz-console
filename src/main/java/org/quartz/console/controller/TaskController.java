package org.quartz.console.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.console.bean.Task;
import org.quartz.console.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
	
	@Autowired
	private TaskMapper taskMapper;
	
	@RequestMapping(value="add_task", method=RequestMethod.POST)
	public Task addTask(@RequestBody Task task) {
		if(taskMapper.insert(task) > 0) {
			return task;
		}
		return null;
	}
	
	@RequestMapping(value="update_task", method=RequestMethod.POST)
	public Task updateTask(@RequestBody Task task) {
		if(taskMapper.updateSelective(task) > 0) {
			return task;
		}
		return null;
	}
	
	@RequestMapping(value="delete_task", method=RequestMethod.GET)
	public Boolean deleteTask(String taskName, String taskOwner) {
		Task task = new Task();
		task.setName(taskName);
		task.setOwner(taskOwner);
		if(taskMapper.delete(task) > 0) {
			return true;
		}
		return false;
	}
	
	@RequestMapping(value="query_task_list", method=RequestMethod.GET)
	public List<Task> queryTaskList(String taskOwner) {
		Map<String, Object> params = new HashMap<>();
		params.put("owner", taskOwner);
		return taskMapper.selectList(params);
	}

}
