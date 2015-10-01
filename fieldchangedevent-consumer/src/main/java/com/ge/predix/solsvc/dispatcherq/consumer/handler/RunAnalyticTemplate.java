package com.ge.predix.solsvc.dispatcherq.consumer.handler;

import org.mimosa.osacbmv3_3.OsacbmDataType;

import com.ge.dsp.pm.analytic.entity.runanalytic.RunAnalyticRequest;
import com.ge.dsp.pm.ext.entity.analytic.port.Port;
import com.ge.dsp.pm.ext.entity.analytic.port.portidentifier.PortIdentifier;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.fieldselection.FieldSelection;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.selectionfilter.SelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;

public class RunAnalyticTemplate {

    @SuppressWarnings("nls")
    public static RunAnalyticRequest getActualRunAnalyticFromTemplate(Long solutionId, String portFieldName, String assetUriName, String assetUriValue)
    {
        RunAnalyticRequest request = new RunAnalyticRequest();

        // Solution Id
        // ============================
        SolutionIdentifier solnId = new SolutionIdentifier();
        solnId.setId(solutionId);
        request.setSolutionIdentifier(solnId);

        // Input Ports
        // =============================

        // SELECT Clause
        // Input Port No.1
        String port1PortId = "ALARM_HI";
        String port1PortName = "ALARM_HI";
        String port1Field = "/asset/assetMeter/" + portFieldName + "/outputMaximum";
        String port1ExpectedDataType = OsacbmDataType.DM_REAL.value();
        String port1FieldSource = FieldSourceEnum.PREDIX_ASSET.name();

        // create SelectionFilter to look for an asset by id
        FieldSelectionFilter assetIdSelectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue assetIdfieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldId = new FieldIdentifier();
        assetIdFieldId.setId(assetUriName);
        assetIdFieldId.setSource(port1FieldSource);
        assetIdfieldIdentifierValue.setFieldIdentifier(assetIdFieldId);
        assetIdfieldIdentifierValue.setValue(assetUriValue);
        assetIdSelectionFilter.getFieldIdentifierValue().add(assetIdfieldIdentifierValue);

        Port velocityHiPort = createPort(port1PortId, port1PortName, port1Field, port1FieldSource,
                port1ExpectedDataType, assetIdSelectionFilter);

        // Input Port No. 2
        String port2PortId = "ALARM_LO";
        String port2PortName = "ALARM_LO";
        String port2Field = "/asset/assetMeter/" + portFieldName + "/outputMinimum";
        String port2ExpectedDataType = OsacbmDataType.DM_REAL.value();
        String port2FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        // reuse the asset id SelectionFilter
        Port velocityLoPort = createPort(port2PortId, port2PortName, port2Field, port2FieldSource,
                port2ExpectedDataType, assetIdSelectionFilter);

        // Input Port No. 3
        String port3PortId = "TS_DATA";
        String port3PortName = "TS_DATA";
        String port3Field = "/asset/assetMeter/" + portFieldName;
        String port3ExpectedDataType = OsacbmDataType.DM_DATA_SEQ.value();
        String port3FieldSource = FieldSourceEnum.PREDIX_TIMESERIES.name();

        // create SelectionFilter to look for an asset by id and also add
        // startTime and endTime (TODO: change this to a new type of
        // SelectionFilter with better semantics)
        String startTimeField = "startTime";
        String startTime = "2015-08-01 11:00:00";
        String endTimeField = "endTime";
        String endTimeValue = "2015-08-08 23:00:00";
        SelectionFilter tsFieldSelectionFilter = getTimeseriesFieldSelectionFilter(assetUriName, assetUriValue,
                startTimeField, startTime, endTimeField, endTimeValue);
        Port velocityPort = createPort(port3PortId, port3PortName, port3Field, port3FieldSource, port3ExpectedDataType,
                tsFieldSelectionFilter);

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
        String outputPort1Field = "/asset/assetMeter/" + portFieldName + "/meterDatasource/meterExtensions/attributes/alertStatus/value";
        String outputPort1FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        Port alarmStatusPort = createPort(outputPort1PortId, outputPort1PortName, outputPort1Field,
                outputPort1FieldSource, null, assetIdSelectionFilter);

        // Output Port No. 2
        String outputPort2PortId = "ALARM_LEVEL";
        String outputPort2PortName = "ALARM_LEVEL";
        String outputPort2Field = "/asset/assetMeter/" + portFieldName + "/meterDatasource/meterExtensions/attributes/alertLevel/value";
        String outputPort2FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        Port alarmLevelPort = createPort(outputPort2PortId, outputPort2PortName, outputPort2Field,
                outputPort2FieldSource, null, assetIdSelectionFilter);

        // Output Port No. 3
        String outputPort3PortId = "ALARM_LEVEL_VALUE";
        String outputPort3PortName = "ALARM_LEVEL_VALUE";
        String outputPort3Field = "/asset/assetMeter/" + portFieldName + "/meterDatasource/meterExtensions/attributes/alertLevelValue/value";
        String outputPort3FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        Port alarmLevelValuePort = createPort(outputPort3PortId, outputPort3PortName, outputPort3Field,
                outputPort3FieldSource, null, assetIdSelectionFilter);

        // Output Port No. 4
        String outputPort4PortId = "ALARM_LEVEL_VALUE_TIME";
        String outputPort4PortName = "ALARM_LEVEL_VALUE_TIME";
        String outputPort4Field = "/asset/assetMeter/" + portFieldName + "/meterDatasource/meterExtensions/attributes/alertTime/value";
        String outputPort4FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        Port alarmLevelValueTimePort = createPort(outputPort4PortId, outputPort4PortName, outputPort4Field,
                outputPort4FieldSource, null, assetIdSelectionFilter);

        // Output Port No. 5
        String outputPort5PortId = "ALARM_THRESHOLDDIFF";
        String outputPort5PortName = "ALARM_THRESHOLDDIFF";
        String outputPort5Field = "/asset/assetMeter/" + portFieldName + "/meterDatasource/meterExtensions/attributes/deltaThreshold/value";
        String outputPort5FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        Port alarmThresholdPort = createPort(outputPort5PortId, outputPort5PortName, outputPort5Field,
                outputPort5FieldSource, null, assetIdSelectionFilter);

        // Output Port No. 6
        String outputPort6PortId = "ALARM_THRESHOLDLEVEL";
        String outputPort6PortName = "ALARM_THRESHOLDLEVEL";
        String outputPort6Field = "/asset/assetMeter/" + portFieldName + "/meterDatasource/meterExtensions/attributes/deltaThresholdLevel/value";
        String outputPort6FieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        Port alarmThresholdLevelPort = createPort(outputPort6PortId, outputPort6PortName, outputPort6Field,
                outputPort6FieldSource, null, assetIdSelectionFilter);

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
    
    private static Port createPort(String portId, String portName, String field, String fieldSource, String expectedDataType,
            SelectionFilter selectionFilter)
    {
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

        // physical selection filter where clause
        aPort.setSelectionFilter(selectionFilter);

        return aPort;
    }
    
    private static SelectionFilter getTimeseriesFieldSelectionFilter(String assetUriField, String assetUriValue,
            String startTimeField, String startTime, String endTimeField, String endTimeValue)
    {
        FieldSelectionFilter tsFieldSelectionFilter = new FieldSelectionFilter();
        // add FieldIdValue pair for assetId
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId(assetUriField);
        assetIdFieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
        fieldIdentifierValue.setValue(assetUriValue);
        tsFieldSelectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
        FieldIdentifierValue startTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier startTimeFieldIdentifier = new FieldIdentifier();
        startTimeFieldIdentifier.setId(startTimeField);
        startTimefieldIdentifierValue.setFieldIdentifier(startTimeFieldIdentifier);
        // fieldIdentifierValue.setValue("1438906239475");
        startTimefieldIdentifierValue.setValue(startTime);
        tsFieldSelectionFilter.getFieldIdentifierValue().add(startTimefieldIdentifierValue);

        FieldIdentifierValue endTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier endTimeFieldIdentifier = new FieldIdentifier();
        endTimeFieldIdentifier.setId(endTimeField);
        endTimefieldIdentifierValue.setFieldIdentifier(endTimeFieldIdentifier);
        // fieldIdentifierValue.setValue("1438906239475");
        endTimefieldIdentifierValue.setValue(endTimeValue);
        tsFieldSelectionFilter.getFieldIdentifierValue().add(endTimefieldIdentifierValue);
        return tsFieldSelectionFilter;
    }
}
