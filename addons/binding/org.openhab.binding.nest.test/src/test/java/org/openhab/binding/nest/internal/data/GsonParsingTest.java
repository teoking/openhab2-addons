/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nest.internal.data;

import static org.junit.Assert.*;
import static org.openhab.binding.nest.internal.data.NestDataUtil.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonParsingTest {

    private final Logger logger = LoggerFactory.getLogger(GsonParsingTest.class);

    @Test
    public void verifyCompleteInput() throws IOException {
        TopLevelData topLevel = fromJson("top-level-data.json", TopLevelData.class);

        assertEquals(topLevel.getDevices().getThermostats().size(), 1);
        assertNotNull(topLevel.getDevices().getThermostats().get(THERMOSTAT1_DEVICE_ID));
        assertEquals(topLevel.getDevices().getCameras().size(), 2);
        assertNotNull(topLevel.getDevices().getCameras().get(CAMERA1_DEVICE_ID));
        assertNotNull(topLevel.getDevices().getCameras().get(CAMERA2_DEVICE_ID));
        assertEquals(topLevel.getDevices().getSmokeDetectors().size(), 4);
        assertNotNull(topLevel.getDevices().getSmokeDetectors().get(SMOKE1_DEVICE_ID));
        assertNotNull(topLevel.getDevices().getSmokeDetectors().get(SMOKE2_DEVICE_ID));
        assertNotNull(topLevel.getDevices().getSmokeDetectors().get(SMOKE3_DEVICE_ID));
        assertNotNull(topLevel.getDevices().getSmokeDetectors().get(SMOKE4_DEVICE_ID));
    }

    @Test
    public void verifyCompleteStreamingInput() throws IOException {
        TopLevelStreamingData topLevelStreamingData = fromJson("top-level-streaming-data.json",
                TopLevelStreamingData.class);

        assertEquals("/", topLevelStreamingData.getPath());

        TopLevelData data = topLevelStreamingData.getData();
        assertEquals(data.getDevices().getThermostats().size(), 1);
        assertNotNull(data.getDevices().getThermostats().get(THERMOSTAT1_DEVICE_ID));
        assertEquals(data.getDevices().getCameras().size(), 2);
        assertNotNull(data.getDevices().getCameras().get(CAMERA1_DEVICE_ID));
        assertNotNull(data.getDevices().getCameras().get(CAMERA2_DEVICE_ID));
        assertEquals(data.getDevices().getSmokeDetectors().size(), 4);
        assertNotNull(data.getDevices().getSmokeDetectors().get(SMOKE1_DEVICE_ID));
        assertNotNull(data.getDevices().getSmokeDetectors().get(SMOKE2_DEVICE_ID));
        assertNotNull(data.getDevices().getSmokeDetectors().get(SMOKE3_DEVICE_ID));
        assertNotNull(data.getDevices().getSmokeDetectors().get(SMOKE4_DEVICE_ID));
    }

    @Test
    public void verifyThermostat() throws IOException {
        Thermostat thermostat = fromJson("thermostat-data.json", Thermostat.class);
        logger.debug("Thermostat: {}", thermostat);

        assertTrue(thermostat.isOnline());
        assertTrue(thermostat.isCanHeat());
        assertTrue(thermostat.isHasLeaf());
        assertFalse(thermostat.isCanCool());
        assertFalse(thermostat.isFanTimerActive());
        assertFalse(thermostat.isLocked());
        assertFalse(thermostat.isSunlightCorrectionActive());
        assertTrue(thermostat.isSunlightCorrectionEnabled());
        assertFalse(thermostat.isUsingEmergencyHeat());
        assertEquals(THERMOSTAT1_DEVICE_ID, thermostat.getDeviceId());
        assertEquals(Integer.valueOf(15), thermostat.getFanTimerDuration());
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal.set(2017, 1, 2, 21, 0, 6);
        assertEquals(utcCal.getTime().toString(), thermostat.getLastConnection().toString());
        utcCal.set(1970, 0, 1, 0, 0, 0);
        assertEquals(utcCal.getTime().toString(), thermostat.getFanTimerTimeout().toString());
        assertEquals(Double.valueOf(22.0), thermostat.getLockedTemperatureHigh());
        assertEquals(Double.valueOf(20.0), thermostat.getLockedTemperatureLow());
        assertEquals(Thermostat.Mode.HEAT, thermostat.getMode());
        assertEquals("Living Room (Living Room)", thermostat.getName());
        assertEquals("Living Room Thermostat (Living Room)", thermostat.getNameLong());
        assertEquals(null, thermostat.getPreviousMode());
        assertEquals("5.6-7", thermostat.getSoftwareVersion());
        assertEquals(Thermostat.State.OFF, thermostat.getState());
        assertEquals(STRUCTURE1_STRUCTURE_ID, thermostat.getStructureId());
        assertEquals(Double.valueOf(15.5), thermostat.getTargetTemperature());
        assertEquals(Double.valueOf(24.0), thermostat.getTargetTemperatureHigh());
        assertEquals(Double.valueOf(20.0), thermostat.getTargetTemperatureLow());
        assertEquals(SIUnits.CELSIUS, thermostat.getTemperatureUnit());
        assertEquals(Integer.valueOf(0), thermostat.getTimeToTarget());
        assertEquals(THERMOSTAT1_WHERE_ID, thermostat.getWhereId());
        assertEquals("Living Room", thermostat.getWhereName());
    }

    @Test
    public void thermostatTimeToTargetSupportedValueParsing() {
        assertEquals((Integer) 0, Thermostat.parseTimeToTarget("~0"));
        assertEquals((Integer) 5, Thermostat.parseTimeToTarget("<5"));
        assertEquals((Integer) 10, Thermostat.parseTimeToTarget("<10"));
        assertEquals((Integer) 15, Thermostat.parseTimeToTarget("~15"));
        assertEquals((Integer) 90, Thermostat.parseTimeToTarget("~90"));
        assertEquals((Integer) 120, Thermostat.parseTimeToTarget(">120"));
    }

    @Test(expected = NumberFormatException.class)
    public void thermostatTimeToTargetUnsupportedValueParsing() {
        Thermostat.parseTimeToTarget("#5");
    }

    @Test
    public void verifyCamera() throws IOException {
        Camera camera = fromJson("camera-data.json", Camera.class);
        logger.debug("Camera: {}", camera);

        assertTrue(camera.isOnline());
        assertEquals("Upstairs", camera.getName());
        assertEquals("Upstairs Camera", camera.getNameLong());
        assertEquals(STRUCTURE1_STRUCTURE_ID, camera.getStructureId());
        assertEquals(CAMERA1_WHERE_ID, camera.getWhereId());
        assertTrue(camera.isAudioInputEnabled());
        assertFalse(camera.isPublicShareEnabled());
        assertFalse(camera.isStreaming());
        assertFalse(camera.isVideoHistoryEnabled());
        assertEquals("nestmobile://cameras/CjZfTEs4ajlyUlh3Q0tFQk90RG83SnNrTnh6V2ZIQk9JbTNDTG91Q1QzRlFaenJ2b2"
                + "tLX0R6RlESFm9wNVB2NW93NmJ6cUdvMkZQSGUxdEEaNld0Mkl5b2tIR0tKX2FpUVd1SkRnQjc2ejhSWFl3SFFxWXFrSWx2QlpxN1g"
                + "yeWNqdmRZVjdGQQ?auth=c.eQ5QBBPiFOTNzPHbmZPcE9yPZ7GayzLusifgQR2DQRFNyUS9ESvlhJF0D7vG8Y0TFV39zX1vIOsWrv"
                + "8RKCMrFepNUb9FqHEboa4MtWLUsGb4tD9oBh0jrV4HooJUmz5sVA5KZR0dkxyLYyPc", camera.getAppUrl());
        assertEquals(CAMERA1_DEVICE_ID, camera.getDeviceId());
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 2017-01-22T08:19:20.000Z
        utcCal.set(2017, 0, 22, 8, 19, 20);
        assertNull(camera.getLastConnection());
        assertEquals(utcCal.getTime().toString(), camera.getLastIsOnlineChange().toString());
        assertNull(camera.getPublicShareUrl());
        assertEquals("https://www.dropcam.com/api/wwn.get_snapshot/CjZfTEs4ajlyUlh3Q0tFQk90RG83SnNrTnh6V"
                + "2ZIQk9JbTNDTG91Q1QzRlFaenJ2b2tLX0R6RlESFm9wNVB2NW93NmJ6cUdvMkZQSGUxdEEaNld0Mkl5b2tIR0tKX2FpUVd1SkRnQj"
                + "c2ejhSWFl3SFFxWXFrSWx2QlpxN1gyeWNqdmRZVjdGQQ?auth=c.eQ5QBBPiFOTNzPHbmZPcE9yPZ7GayzLusifgQR2DQRFNyUS9E"
                + "SvlhJF0D7vG8Y0TFV39zX1vIOsWrv8RKCMrFepNUb9FqHEboa4MtWLUsGb4tD9oBh0jrV4HooJUmz5sVA5KZR0dkxyLYyPc",
                camera.getSnapshotUrl());
        assertEquals("205-600052", camera.getSoftwareVersion());
        assertEquals("https://home.nest.com/cameras/CjZfTEs4ajlyUlh3Q0tFQk90RG83SnNrTnh6V2ZIQk9JbTNDTG91Q1QzR"
                + "lFaenJ2b2tLX0R6RlESFm9wNVB2NW93NmJ6cUdvMkZQSGUxdEEaNld0Mkl5b2tIR0tKX2FpUVd1SkRnQjc2ejhSWFl3SFFxWXFrSW"
                + "x2QlpxN1gyeWNqdmRZVjdGQQ?auth=c.eQ5QBBPiFOTNzPHbmZPcE9yPZ7GayzLusifgQR2DQRFNyUS9ESvlhJF0D7vG8Y0TFV39z"
                + "X1vIOsWrv8RKCMrFepNUb9FqHEboa4MtWLUsGb4tD9oBh0jrV4HooJUmz5sVA5KZR0dkxyLYyPc", camera.getWebUrl());
        assertEquals("animeted", camera.getLastEvent().getAnimatedImageUrl());
        assertEquals(2, camera.getLastEvent().getActivityZones().size());
        assertEquals("id1", camera.getLastEvent().getActivityZones().get(0));
        assertEquals("app_url", camera.getLastEvent().getAppUrl());
        // 2017-01-22T07:40:38.680Z
        utcCal.set(2017, 0, 22, 7, 40, 38);
        assertEquals(utcCal.getTime().toString(), camera.getLastEvent().getEndTime().toString());
        assertEquals("image_url", camera.getLastEvent().getImageUrl());
        // 2017-01-22T07:40:19.020Z
        utcCal.set(2017, 0, 22, 7, 40, 19);
        assertEquals(utcCal.getTime().toString(), camera.getLastEvent().getStartTime().toString());
        assertNull(camera.getLastEvent().getUrlsExpireTime());
        assertEquals("myurl", camera.getLastEvent().getWebUrl());
        assertTrue(camera.getLastEvent().isHasMotion());
        assertFalse(camera.getLastEvent().isHasPerson());
        assertFalse(camera.getLastEvent().isHasSound());
    }

    @Test
    public void verifySmokeDetector() throws IOException {
        SmokeDetector smokeDetector = fromJson("smoke-detector-data.json", SmokeDetector.class);
        logger.debug("SmokeDetector: {}", smokeDetector);

        assertTrue(smokeDetector.isOnline());
        assertEquals(SMOKE1_WHERE_ID, smokeDetector.getWhereId());
        assertEquals(SMOKE1_DEVICE_ID, smokeDetector.getDeviceId());
        assertEquals("Downstairs", smokeDetector.getName());
        assertEquals("Downstairs Nest Protect", smokeDetector.getNameLong());
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 2017-02-02T20:53:05.338Z
        utcCal.set(2017, 1, 2, 20, 53, 5);
        assertEquals(utcCal.getTime().toString(), smokeDetector.getLastConnection().toString());
        assertEquals(SmokeDetector.BatteryHealth.OK, smokeDetector.getBatteryHealth());
        assertEquals(SmokeDetector.AlarmState.OK, smokeDetector.getCoAlarmState());
        assertEquals(SmokeDetector.AlarmState.OK, smokeDetector.getSmokeAlarmState());
        assertEquals("3.1rc9", smokeDetector.getSoftwareVersion());
        assertEquals(STRUCTURE1_STRUCTURE_ID, smokeDetector.getStructureId());
        assertEquals(SmokeDetector.UiColorState.GREEN, smokeDetector.getUiColorState());
    }

    @Test
    public void verifyAccessToken() throws IOException {
        AccessTokenData accessToken = fromJson("access-token-data.json", AccessTokenData.class);
        logger.debug("AccessTokenData: {}", accessToken);

        assertEquals("access_token", accessToken.getAccessToken());
        assertEquals(Long.valueOf(315360000L), accessToken.getExpiresIn());
    }

    @Test
    public void verifyStructure() throws IOException {
        Structure structure = fromJson("structure-data.json", Structure.class);
        logger.debug("Structure: {}", structure);

        assertEquals("Home", structure.getName());
        assertEquals("US", structure.getCountryCode());
        assertEquals("98056", structure.getPostalCode());
        assertEquals(Structure.HomeAwayState.HOME, structure.getAway());
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 2017-02-02T03:10:08.000Z
        utcCal.set(2017, 1, 2, 3, 10, 8);
        assertEquals(utcCal.getTime().toString(), structure.getEtaBegin().toString());
        assertNull(structure.getEta());
        assertNull(structure.getPeakPeriodEndTime());
        assertNull(structure.getPeakPeriodStartTime());
        assertEquals(STRUCTURE1_STRUCTURE_ID, structure.getStructureId());
        assertEquals("America/Los_Angeles", structure.getTimeZone());
        assertFalse(structure.isRushHourRewardsEnrollement());
    }

    @Test
    public void verifyError() throws IOException {
        ErrorData error = fromJson("error-data.json", ErrorData.class);
        logger.debug("ErrorData: {}", error);

        assertEquals("blocked", error.getError());
        assertEquals("https://developer.nest.com/documentation/cloud/error-messages#blocked", error.getType());
        assertEquals("blocked", error.getMessage());
        assertEquals("bb514046-edc9-4bca-8239-f7a3cfb0925a", error.getInstance());
    }

}
