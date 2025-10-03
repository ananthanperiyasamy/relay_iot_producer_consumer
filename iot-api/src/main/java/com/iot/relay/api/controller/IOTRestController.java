package com.iot.relay.api.controller;

import java.math.BigDecimal;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.iot.relay.api.request.QueryRequest;
import com.iot.relay.api.service.IOTOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

/*
 * REST controller for performing query operations on IoT sensor data stored in MongoDB.
 * Provides an endpoint to execute aggregation or calculation operations (like sum, avg, max, min)
 * on sensor data based on a specified time range or other query criteria.
 * Uses {@link IOTOperationService} to perform actual business logic.
 * Author: Ananthan Periyasamy
 */
@RestController
@CrossOrigin
@RequestMapping("query/")
@AllArgsConstructor
public class IOTRestController {

	private IOTOperationService sensorOperationService;

	
	 /*
     * Endpoint to perform a query operation on sensor data.
     * The operation to perform is specified as a path variable (e.g., "sum", "avg").
     * The query parameters (dates, filters) are passed in {@link QueryRequest}.
     * 
     * @param operation the operation type to execute (sum, avg, max, min, etc.)
     * @param queryDTO  validated query request object containing filter criteria
     * @return ResponseEntity containing the result of the operation as BigDecimal
     * @throws Exception if any error occurs during query execution
     */
	@Operation(summary = "Perform query operation on sensor data.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = " Result of the operation."),
			@ApiResponse(responseCode = "400", description = "Invalid input. Dates are mandatory and check date format."),
			@ApiResponse(responseCode = "416", description = "Data for the requested range is not found") 
			})
	@GetMapping(value = "{operation}")
	public ResponseEntity<BigDecimal> performOperation(
			@PathVariable("operation") String operation,
			@Valid QueryRequest queryDTO) throws Exception {
		return new ResponseEntity<>(sensorOperationService.execute(operation, queryDTO), HttpStatus.OK);
	}

}
