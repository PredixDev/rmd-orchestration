package com.ge.predix.solsvc.dispatcherq.consumer.handler;

import java.util.ArrayList;
import java.util.List;

import org.mimosa.osacbmv3_3.OsacbmDataType;

import com.ge.predix.entity.analytic.port.Port;
import com.ge.predix.entity.analytic.port.portidentifier.PortIdentifier;
import com.ge.predix.entity.analytic.runanalytic.RunAnalyticRequest;
import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseriesfilter.TimeseriesFilter;

/**
 * 
 * @author 212546387 -
 */
public class RunAnalyticTemplate {

	/**
	 * @param solutionId
	 *            - solutionId
	 * @param portFieldName
	 *            - portFieldName
	 * @param assetUriName
	 *            - assetUriName
	 * @param assetUriValue
	 *            - assetUriValue
	 * @param assetIdFieldIdentifier
	 *            - assetIdFieldIdentifier
	 * @return -
	 */
	@SuppressWarnings("nls")
	public static RunAnalyticRequest getActualRunAnalyticFromTemplate(String portFieldName,
			String assetUriName, String assetUriValue, FieldIdentifier assetIdFieldIdentifier) {
		RunAnalyticRequest request = new RunAnalyticRequest();

		// Input Ports
		// =============================

		// SELECT Clause
		// Input Port No.1
		String port1PortId = "ALARM_HI";
		String port1PortName = "ALARM_HI";
		// String port1Field = "/asset/assetTag/" + portFieldName +
		// "/outputMaximum";
		String port1Field = portFieldName + "/outputMaximum";
		String port1ExpectedDataType = OsacbmDataType.DM_REAL.value();
		String port1FieldSource = FieldSourceEnum.PREDIX_ASSET.name();

		// create Filter to look for an asset by id
		FieldIdentifierValue assetIdfieldIdentifierValue = new FieldIdentifierValue();
		FieldIdentifier assetIdFieldId = new FieldIdentifier();
		assetIdFieldId.setId(assetUriName);
		assetIdFieldId.setSource(port1FieldSource);
		assetIdfieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
		assetIdfieldIdentifierValue.setValue(assetUriValue);

		AssetFilter assetFilter = new AssetFilter();
		assetFilter.setUri(assetUriValue);
		
		Port velocityHiPort = createPort(port1PortId, port1PortName, port1Field, port1FieldSource,
				port1ExpectedDataType, assetFilter);

		// Input Port No. 2
		String port2PortId = "ALARM_LO";
		String port2PortName = "ALARM_LO";
		// String port2Field = "/asset/assetTag/" + portFieldName +
		// "/outputMinimum";
		String port2Field = portFieldName + "/outputMinimum";
		String port2ExpectedDataType = OsacbmDataType.DM_REAL.value();
		String port2FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		// reuse the asset id Filter
		Port velocityLoPort = createPort(port2PortId, port2PortName, port2Field, port2FieldSource,
				port2ExpectedDataType, assetFilter);

		// Input Port No. 3
		String port3PortId = "TS_DATA";
		String port3PortName = "TS_DATA";
		// String port3Field = "/asset/assetTag/" + portFieldName;
		String port3Field = portFieldName;
		String port3ExpectedDataType = OsacbmDataType.DM_DATA_SEQ.value();
		String port3FieldSource = FieldSourceEnum.PREDIX_TIMESERIES.name();

		// create Filter to look for an asset by id and also add
		// startTime and endTime (TODO: change this to a new type of
		// Filter with better semantics)
		String startTimeField = "startTime";
		String startTime = "2015-08-01 11:00:00";
		String endTimeField = "endTime";
		String endTimeValue = "2015-08-08 23:00:00";
		Filter tsFilter = getTimeseriesFilter(assetUriName, assetUriValue, startTimeField, startTime,
				endTimeField, endTimeValue);
		Port velocityPort = createPort(port3PortId, port3PortName, port3Field, port3FieldSource, port3ExpectedDataType,
				tsFilter);

		// Add Input Ports to Analytic request
		request.getInputPort().add(velocityHiPort);
		request.getInputPort().add(velocityLoPort);
		request.getInputPort().add(velocityPort);

		// ===========================================================================================================

		// Output Ports
		// =============================
		// Output Port No. 1
		String outputPort1PortId = "ALARM_STATUS";
		String outputPort1PortName = "ALARM_STATUS";
		// String outputPort1Field = "/asset/assetTag/" + portFieldName +
		// "/tagDatasource/tagExtensions/attributes/alertStatus/value";
		String outputPort1Field = portFieldName + "/tagDatasource/tagExtensions/attributes/alertStatus/value";
		String outputPort1FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		Port alarmStatusPort = createPort(outputPort1PortId, outputPort1PortName, outputPort1Field,
				outputPort1FieldSource, null, assetFilter);

		// Output Port No. 2
		String outputPort2PortId = "ALARM_LEVEL";
		String outputPort2PortName = "ALARM_LEVEL";
		// String outputPort2Field = "/asset/assetTag/" + portFieldName +
		// "/tagDatasource/tagExtensions/attributes/alertLevel/value";
		String outputPort2Field = portFieldName + "/tagDatasource/tagExtensions/attributes/alertLevel/value";
		String outputPort2FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		Port alarmLevelPort = createPort(outputPort2PortId, outputPort2PortName, outputPort2Field,
				outputPort2FieldSource, null, assetFilter);

		// Output Port No. 3
		String outputPort3PortId = "ALARM_LEVEL_VALUE";
		String outputPort3PortName = "ALARM_LEVEL_VALUE";
		// String outputPort3Field = "/asset/assetTag/" + portFieldName +
		// "/tagDatasource/tagExtensions/attributes/alertLevelValue/value";
		String outputPort3Field = portFieldName + "/tagDatasource/tagExtensions/attributes/alertLevelValue/value";
		String outputPort3FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		Port alarmLevelValuePort = createPort(outputPort3PortId, outputPort3PortName, outputPort3Field,
				outputPort3FieldSource, null, assetFilter);

		// Output Port No. 4
		String outputPort4PortId = "ALARM_LEVEL_VALUE_TIME";
		String outputPort4PortName = "ALARM_LEVEL_VALUE_TIME";
		// String outputPort4Field = "/asset/assetTag/" + portFieldName +
		// "/tagDatasource/tagExtensions/attributes/alertTime/value";
		String outputPort4Field = portFieldName + "/tagDatasource/tagExtensions/attributes/alertTime/value";
		String outputPort4FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		Port alarmLevelValueTimePort = createPort(outputPort4PortId, outputPort4PortName, outputPort4Field,
				outputPort4FieldSource, null, assetFilter);

		// Output Port No. 5
		String outputPort5PortId = "ALARM_THRESHOLDDIFF";
		String outputPort5PortName = "ALARM_THRESHOLDDIFF";
		// String outputPort5Field = "/asset/assetTag/" + portFieldName +
		// "/tagDatasource/tagExtensions/attributes/deltaThreshold/value";
		String outputPort5Field = portFieldName + "/tagDatasource/tagExtensions/attributes/deltaThreshold/value";
		String outputPort5FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		Port alarmThresholdPort = createPort(outputPort5PortId, outputPort5PortName, outputPort5Field,
				outputPort5FieldSource, null, assetFilter);

		// Output Port No. 6
		String outputPort6PortId = "ALARM_THRESHOLDLEVEL";
		String outputPort6PortName = "ALARM_THRESHOLDLEVEL";
		// String outputPort6Field = "/asset/assetTag/" + portFieldName +
		// "/tagDatasource/tagExtensions/attributes/deltaThresholdLevel/value";
		String outputPort6Field = portFieldName + "/tagDatasource/tagExtensions/attributes/deltaThresholdLevel/value";
		String outputPort6FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
		Port alarmThresholdLevelPort = createPort(outputPort6PortId, outputPort6PortName, outputPort6Field,
				outputPort6FieldSource, null, assetFilter);

		// Add output ports
		request.getOutputPort().add(alarmStatusPort);
		request.getOutputPort().add(alarmLevelPort);
		request.getOutputPort().add(alarmLevelValuePort);
		request.getOutputPort().add(alarmLevelValueTimePort);
		request.getOutputPort().add(alarmThresholdPort);
		request.getOutputPort().add(alarmThresholdLevelPort);
		request.setExternalAttributeMap(null);

		return request;
	}

	private static Port createPort(String portId, String portName, String field, String fieldSource,
			String expectedDataType, Filter filter) {
		Port aPort = new Port();
		PortIdentifier inputPortId = new PortIdentifier();
		inputPortId.setId(portId);
		inputPortId.setName(portName);

		// logical
		aPort.setPortIdentifier(inputPortId);
		// physical
		FieldSelection fieldSelection = new FieldSelection();
		FieldIdentifier fieldIdentifier = new FieldIdentifier();
		fieldIdentifier.setId(field);
		fieldIdentifier.setName(field);
		fieldIdentifier.setSource(fieldSource);
		fieldSelection.setFieldIdentifier(fieldIdentifier);
		fieldSelection.setExpectedDataType(expectedDataType);
		fieldSelection.setResultId(portId);

		aPort.setFieldSelection(fieldSelection);

		// physical filter where clause
		aPort.setFilter(filter);

		return aPort;
	}

	@SuppressWarnings("unused")
	private static Filter getTimeseriesFilter(String assetUriField, String assetUriValue, String startTimeField,
			String startTime, String endTimeField, String endTimeValue) {


		TimeseriesFilter tsFilter = new TimeseriesFilter();						
		
		// SET Time Series Filter
		DatapointsQuery query = new DatapointsQuery();
		query.setStart("5d-ago");
		query.setEnd(null);
		com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
		//tag.setName("Compressor-2015:DischargePressure");
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
		tags.add(tag);
		query.setTags(tags);
		
		tsFilter.setDatapointsQuery(query);
		
		
		return tsFilter;
	}
}
