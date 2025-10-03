package com.iot.relay.consumer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iot.relay.consumer.mapper.IOTDataMapper;
import com.iot.relay.model.IOTData;
import com.iot.relay.model.IOTDataEntity;
import com.iot.relay.repository.IOTDataRepository;

@Service
public class IOTEventService {
	
	@Autowired
	private IOTDataMapper iotDataMapper;
	@Autowired
	private IOTDataRepository iotDataRepository;

	/**
	 * Save the sensor data in repository
	 * 
	 * @param sensorDataEntity
	 */
	public void save(IOTData iotData) {
		System.out.println("Saving this data "+ iotData.getTimestamp());
		iotDataRepository.save(iotDataMapper.fromEventToEntity(iotData));
	}

	
	 public void saveAll(List<IOTData> iotDataList) {
	     List<IOTDataEntity> entities = iotDataList.stream()
	            .map(iotDataMapper::fromEventToEntity)
	            .collect(Collectors.toList());
	     iotDataRepository.saveAll(entities);
	 }
}
