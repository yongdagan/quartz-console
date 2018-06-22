package org.quartz.console.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.console.bean.Task;
import org.quartz.console.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskMapperTest {
	
	@Autowired
	private TaskMapper taskMapper;
	
	private final String NAME = "test";
	private final String OWNER = "test";
	
	@Before
	public void before() {
		Task task = getTask(OWNER, NAME);
		if(task != null) {
			return;
		}
		
		task = new Task();
		task.setName(NAME);
		task.setOwner(OWNER);
		task.setTargetJob("job");
		task.setTriggerType(Constant.TRIGGER_TYPE_INTERVAL);
		task.setRepeatSeconds(10);
		taskMapper.insert(task);
	}
	
	private Task getTask(String owner, String name) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("name", NAME);
		params.put("owner", OWNER);
		List<Task> list = taskMapper.selectList(params);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@Test
	public void test() {
		Task task = getTask(OWNER, NAME);
		task.setData("taskData");
		taskMapper.updateSelective(task);
		
		Task tmp = getTask(OWNER, NAME);
		Assert.assertTrue(StringUtils.equals(task.getData(), tmp.getData()));
		Assert.assertTrue(Constant.REFRESH_Y.equals(tmp.getRefresh()));
		
		taskMapper.refresh(tmp);
		tmp = getTask(OWNER, NAME);
		Assert.assertTrue(Constant.REFRESH_N.equals(tmp.getRefresh()));
		tmp.setStatus(Constant.STATUS_VALID);
		taskMapper.updateSelective(tmp);
		
		Assert.assertTrue(Constant.STATUS_VALID.equals(tmp.getStatus()));
		taskMapper.delete(tmp);
		tmp = getTask(OWNER, NAME);
		Assert.assertTrue(Constant.STATUS_INVALID.equals(tmp.getStatus()));
		Assert.assertTrue(Constant.REFRESH_Y.equals(tmp.getRefresh()));
		
	}

}
