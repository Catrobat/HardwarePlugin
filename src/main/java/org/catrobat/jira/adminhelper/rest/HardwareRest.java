/*
 * Copyright 2014 Stephan Fellhofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.catrobat.jira.adminhelper.rest;

import com.atlassian.crowd.embedded.api.*;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.plugin.ProjectPermissionOverride;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.helper.HelperUtil;
import org.catrobat.jira.adminhelper.helper.PermissionCondition;
import org.catrobat.jira.adminhelper.rest.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/hardware")
public class HardwareRest extends RestHelper {

    private final UserManager userManager;
    private final HardwareModelService hardwareModelService;
    private final DeviceService deviceService;
    private final LendingService lendingService;
    private final TypeOfDeviceService typeOfDeviceService;
    private final ProducerService producerService;
    private final OperatingSystemService operatingSystemService;
    private final DeviceCommentService deviceCommentService;
    private final PermissionCondition permissionCondition;
    private final ReadOnlyHdwUserService readOnlyUserService;
    private final ReadOnlyHdwGroupService readOnlyHdwGroupService;

    public HardwareRest(UserManager userManager, HardwareModelService hardwareModelService, DeviceService deviceService,
                        LendingService lendingService, TypeOfDeviceService typeOfDeviceService,
                        ProducerService producerService, OperatingSystemService operatingSystemService,
                        DeviceCommentService deviceCommentService, AdminHelperConfigService configurationService,
                        GroupManager groupManager, PermissionManager permissionManager,
                        ReadOnlyHdwUserService readOnlyUserService, ReadOnlyHdwGroupService readOnlyHdwGroupService) {
        super(permissionManager, configurationService, userManager, groupManager);
        super.setHardwareServices(readOnlyHdwGroupService, readOnlyUserService);
        this.userManager = checkNotNull(userManager);
        this.hardwareModelService = checkNotNull(hardwareModelService);
        this.deviceService = checkNotNull(deviceService);
        this.lendingService = checkNotNull(lendingService);
        this.typeOfDeviceService = checkNotNull(typeOfDeviceService);
        this.producerService = checkNotNull(producerService);
        this.operatingSystemService = checkNotNull(operatingSystemService);
        this.deviceCommentService = checkNotNull(deviceCommentService);
        this.permissionCondition = new PermissionCondition(null, configurationService, userManager, groupManager);
        this.readOnlyUserService = checkNotNull(readOnlyUserService);
        this.readOnlyHdwGroupService = checkNotNull(readOnlyHdwGroupService);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHardwareModelList(@Context HttpServletRequest request) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<JsonHardwareModel> hardwareModelList = new ArrayList<JsonHardwareModel>();
        for (HardwareModel hardwareModel : hardwareModelService.all()) {
            hardwareModelList.add(new JsonHardwareModel(hardwareModel, lendingService, deviceService));
        }

        return Response.ok(hardwareModelList).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putHardware(@Context HttpServletRequest request, JsonHardwareModel jsonHardwareModel) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        if (jsonHardwareModel == null)
            return Response.serverError().entity("No hardware model given").build();

        if (jsonHardwareModel.getName() == null || jsonHardwareModel.getName().equals(""))
            return Response.serverError().entity("Name must not be empty").build();

        HardwareModel hardwareModel;
        if (jsonHardwareModel.getId() == 0) {
            hardwareModel = hardwareModelService.add(jsonHardwareModel.getName(),
                    jsonHardwareModel.getTypeOfDevice(), jsonHardwareModel.getVersion(), jsonHardwareModel.getPrice(),
                    jsonHardwareModel.getProducer(), jsonHardwareModel.getOperatingSystem(), jsonHardwareModel.getArticleNumber());
        } else {
            hardwareModel = hardwareModelService.edit(jsonHardwareModel.getId(), jsonHardwareModel.getName(),
                    jsonHardwareModel.getTypeOfDevice(), jsonHardwareModel.getVersion(), jsonHardwareModel.getPrice(),
                    jsonHardwareModel.getProducer(), jsonHardwareModel.getOperatingSystem(), jsonHardwareModel.getArticleNumber());
        }

        if (hardwareModel == null) {
            return Response.serverError().entity("Could not save hardware model (maybe name and version are already existing)").build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypesOfDevices(@Context HttpServletRequest request, String query) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<String> typeList = new ArrayList<String>();
        for (TypeOfDevice typeOfDevice : typeOfDeviceService.searchTypeOfDevice(query)) {
            typeList.add(typeOfDevice.getTypeOfDeviceName());
        }

        return Response.ok(typeList).build();
    }

    @GET
    @Path("/producers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducers(@Context HttpServletRequest request, String query) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<String> producerList = new ArrayList<String>();
        for (Producer producer : producerService.searchProducers(query)) {
            producerList.add(producer.getProducerName());
        }

        return Response.ok(producerList).build();
    }

    @GET
    @Path("/operating-systems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperatingSystems(@Context HttpServletRequest request, String query) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<String> operatingSystemList = new ArrayList<String>();
        for (OperatingSystem operatingSystem : operatingSystemService.searchOperatingSystems(query)) {
            operatingSystemList.add(operatingSystem.getOperatingSystemName());
        }

        return Response.ok(operatingSystemList).build();
    }

    @GET
    @Path("/{hardwareId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHardware(@Context HttpServletRequest request, @PathParam("hardwareId") int hardwareId) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        JsonHardwareModel jsonHardwareModel = new JsonHardwareModel(hardwareModelService.get(hardwareId), lendingService, deviceService);
        return Response.ok(jsonHardwareModel).build();
    }

    @DELETE
    @Path("/{hardwareId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHardware(@Context HttpServletRequest request, @PathParam("hardwareId") int hardwareId, JsonHardwareModel moveToHardwareJson) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }
        int moveToHardwareId = moveToHardwareJson.getId();

        HardwareModel toDelete = hardwareModelService.get(hardwareId);
        if (toDelete == null) {
            return Response.serverError().entity("No hardware model found").build();
        }

        if (toDelete.getDevices().length != 0) {
            HardwareModel moveTo = hardwareModelService.get(moveToHardwareId);
            if (moveTo == null) {
                return Response.serverError().entity("No hardware model to move devices to was found").build();
            }
            hardwareModelService.moveDevices(toDelete, moveTo);
        }

        hardwareModelService.delete(toDelete);

        return Response.noContent().build();
    }

    @PUT
    @Path("/{hardwareId}/devices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDevice(@Context HttpServletRequest request, @PathParam("hardwareId") int hardwareId, JsonDevice jsonDevice) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        if (((jsonDevice.getSerialNumber() == null || jsonDevice.getSerialNumber().equals("")) &&
                (jsonDevice.getImei() == null || jsonDevice.getImei().equals("")) &&
                (jsonDevice.getInventoryNumber() == null || jsonDevice.getInventoryNumber().equals("")))) {
            return Response.serverError().entity("At least one unique identifier must be given").build();
        }

        HardwareModel hardwareModel = hardwareModelService.get(hardwareId);
        if (hardwareModel == null) {
            return Response.serverError().entity("Hardware Device not found").build();
        }

        Device device;
        if (jsonDevice.getId() != 0) {
            device = deviceService.edit(deviceService.getDeviceById(jsonDevice.getId()), hardwareModel, jsonDevice.getImei(),
                    jsonDevice.getSerialNumber(), jsonDevice.getInventoryNumber(), jsonDevice.getReceivedFrom(),
                    jsonDevice.getReceivedDate(), jsonDevice.getUsefulLiveOfAsset(), jsonDevice.getSortedOutDate(), jsonDevice.getSortedOutComment());
        } else {
            device = deviceService.add(hardwareModel, jsonDevice.getImei(), jsonDevice.getSerialNumber(),
                    jsonDevice.getInventoryNumber(), jsonDevice.getReceivedFrom(), jsonDevice.getReceivedDate(),
                    jsonDevice.getUsefulLiveOfAsset());
        }

        if (device == null) {
            return Response.serverError().entity("Device not created - maybe serial/inventory/imei already exists?").build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/{hardwareId}/devices/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevicesAvailableForHardware(@Context HttpServletRequest request, @PathParam("hardwareId") int hardwareId) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<JsonDevice> jsonDeviceList = new ArrayList<JsonDevice>();
        for (Device device : deviceService.getCurrentlyAvailableDevices(hardwareId)) {
            jsonDeviceList.add(new JsonDevice(device));
        }

        return Response.ok(jsonDeviceList).build();
    }

    @PUT
    @Path("/devices/{deviceId}/lend-out")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response lendOutDevice(@Context HttpServletRequest request, @PathParam("deviceId") int deviceId, JsonLending jsonLending) {

        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            return Response.serverError().entity("Device not found").build();
        }

        com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
        ApplicationUser lendingByUser = jiraUserManager.getUserByName(jsonLending.getLentOutBy());
        ApplicationUser lendingIssuerUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        if (lendingIssuerUser == null) {
            return Response.serverError().entity("Issuing user not found").build();
        }

        String lendingByUserName;
        if (lendingByUser == null) {
            lendingByUserName = jsonLending.getLentOutBy();
        } else {
            lendingByUserName = lendingByUser.getKey();
        }

        Date begin = jsonLending.getBegin();
        if (begin == null) {
            begin = new Date();
        }

        Lending lending = lendingService.lendOut(device, lendingByUserName, lendingIssuerUser.getKey(), jsonLending.getPurpose(), jsonLending.getComment(), begin);
        if (lending == null) {
            return Response.serverError().entity("Saving data failed").build();
        }

        if (jsonLending.getDevice() != null && jsonLending.getDevice().getComments() != null &&
                jsonLending.getDevice().getComments().size() != 0 && jsonLending.getDevice().getComments().get(0) != null) {
            deviceCommentService.addDeviceComment(device, lendingIssuerUser.getKey(), jsonLending.getDevice().getComments().get(0).getComment());
        }


        return Response.noContent().build();
    }

    @GET
    @Path("/devices/{deviceId}/current-lending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentLendingForDevice(@Context HttpServletRequest request, @PathParam("deviceId") int deviceId) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            return Response.serverError().entity("No device found").build();
        }

        Lending currentLending = null;
        for (Lending lending : device.getLendings()) {
            if (lending.getEnd() == null) {
                currentLending = lending;
                break;
            }
        }

        if (currentLending == null) {
            return Response.serverError().entity("No current lending for device.").build();
        }

        JsonLending jsonLending = new JsonLending(currentLending, ComponentAccessor.getUserManager());

        return Response.ok(jsonLending).build();
    }

    @PUT
    @Path("/devices/{deviceId}/current-lending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bringBackCurrentLendingForDevice(@Context HttpServletRequest request, @PathParam("deviceId") int deviceId, JsonLending jsonLending) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            return Response.serverError().entity("No device found").build();
        }

        Lending currentLending = null;
        for (Lending lending : device.getLendings()) {
            if (lending.getEnd() == null) {
                currentLending = lending;
                break;
            }
        }

        if (currentLending == null) {
            return Response.serverError().entity("No current lending for device").build();
        }

        // just the date matters - not the time
        if (jsonLending.getEnd() == null || HelperUtil.clearTime(jsonLending.getEnd()).getTime() < HelperUtil.clearTime(currentLending.getBegin()).getTime()) {
            return Response.serverError().entity("No or invalid end date given").build();
        }

        Lending lending = lendingService.bringBack(currentLending, jsonLending.getPurpose(), jsonLending.getComment(), jsonLending.getEnd());
        if (lending == null) {
            return Response.serverError().entity("Saving data failed").build();
        }


        if (jsonLending.getDevice() != null && jsonLending.getDevice().getComments() != null &&
                jsonLending.getDevice().getComments().size() != 0 && jsonLending.getDevice().getComments().get(0) != null) {
            JsonDeviceComment comment = jsonLending.getDevice().getComments().get(0);
            String username = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser().getName();
            ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);
            String userKey = user == null ? username : user.getKey();
            deviceCommentService.addDeviceComment(currentLending.getDevice(), userKey, comment.getComment());
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/received-from")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReceivedFrom(@Context HttpServletRequest request) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        Map<String, JsonDevice> deviceMap = new TreeMap<String, JsonDevice>();
        for (Device device : deviceService.all()) {
            if (device != null && device.getReceivedFrom() != null && !deviceMap.containsKey(device.getReceivedFrom())) {
                JsonDevice jsonDevice = new JsonDevice();
                jsonDevice.setReceivedFrom(device.getReceivedFrom());
                deviceMap.put(device.getReceivedFrom(), jsonDevice);
            }
        }

        return Response.ok(deviceMap.values()).build();
    }

    @GET
    @Path("/devices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@Context HttpServletRequest request) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<Device> deviceList = deviceService.all();
        List<JsonDevice> jsonDeviceList = new ArrayList<JsonDevice>();
        for (Device device : deviceList) {
            jsonDeviceList.add(new JsonDevice(device));
        }

        return Response.ok(jsonDeviceList).build();
    }

    @GET
    @Path("/devices/sorted-out")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSortedOutDevices(@Context HttpServletRequest request) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<JsonDevice> jsonDeviceList = new ArrayList<JsonDevice>();
        for (Device device : deviceService.getSortedOutDevices()) {
            jsonDeviceList.add(new JsonDevice(device));
        }

        return Response.ok(jsonDeviceList).build();
    }

    @GET
    @Path("/devices/ongoing-lending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentlyLentOut(@Context HttpServletRequest request) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        List<JsonDevice> jsonDeviceList = new ArrayList<JsonDevice>();
        for (Lending lending : lendingService.currentlyLentOut()) {
            JsonDevice device = new JsonDevice(lending.getDevice());
            jsonDeviceList.add(device);
        }

        return Response.ok(jsonDeviceList).build();
    }

    @POST
    @Path("/lending/all-for-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLendingForUser(@Context HttpServletRequest request, @FormParam("user") String userKey) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }


        ArrayList<JsonLending> lendingArrayList = new ArrayList<JsonLending>();
        com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
        for (Lending lending : lendingService.searchAllForUser(userKey)) {
            lendingArrayList.add(new JsonLending(lending, jiraUserManager));
        }

        return Response.ok(lendingArrayList).build();
    }

    @GET
    @Path("/devices/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevice(@Context HttpServletRequest request, @PathParam("deviceId") int deviceId) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            return Response.serverError().entity("Device not found").build();
        }

        JsonDevice jsonDevice = new JsonDevice(device, ComponentAccessor.getUserManager());
        return Response.ok(jsonDevice).build();
    }

    @PUT
    @Path("/devices/{deviceId}/sort-out")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sortOutDevice(@Context HttpServletRequest request, @PathParam("deviceId") int deviceId, JsonDevice jsonDevice) {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            return Response.serverError().entity("No device found").build();
        }

        if (jsonDevice == null || jsonDevice.getSortedOutDate() == null) {
            return Response.serverError().entity("No sorted out date given").build();
        }

        device = deviceService.sortOutDevice(deviceId, jsonDevice.getSortedOutDate(), jsonDevice.getSortedOutComment());
        if (device == null) {
            return Response.serverError().entity("Saving data failed").build();
        }


        return Response.noContent().build();
    }

    public boolean saveReadOnlyUsers(List<String> readonly_users) {

        System.out.println("save read only users");
        System.out.println("users are: " + readonly_users);

        List<String> temp_user_keys = new ArrayList<>();

        for (String user : readonly_users) {
            user = user.trim();
            ApplicationUser jira_user = userManager.getUserByName(user);
            String key;

            try {
                key = jira_user.getKey();
            } catch (Exception e) {
                System.out.println("User does not exist");
                return false;
            }
            temp_user_keys.add(key);
        }

        for(String key : temp_user_keys)
            readOnlyUserService.addUserKey(key);

        //returns true if everyting went fine
        return true;
    }

    public boolean saveReadOnlyGroups(List<String> readonly_groups) {
        System.out.println("save read only groups");
        System.out.println("groups are: " + readonly_groups);
        GroupManager group_manager = ComponentAccessor.getGroupManager();

        List<String> temp_groups = new ArrayList<>();

        for (String group : readonly_groups) {
            group = group.trim();
            com.atlassian.crowd.embedded.api.Group temp_group = group_manager.getGroup(group);

            if(temp_group == null)
                return false;
            temp_groups.add(group);
        }
        for(String group_name : temp_groups)
             readOnlyHdwGroupService.setGroupName(group_name);

        return true;
    }

    @GET
    @Path("/isReadOnlyUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response currentUserIsReadOnlyUser(@Context HttpServletRequest request)
    {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        ApplicationUser current_user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        String key = current_user.getKey();
        boolean readonly = readOnlyUserService.isReadOnlyHardwareUser(key);
        JsonReadOnly resp;

        try{
           resp = generateResponseObject(readonly);
        }
        catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        return Response.ok(resp).build();
    }

    @GET
    @Path("/isReadOnlyUser/{User}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response userIsReadonlyUser(@Context HttpServletRequest request, @PathParam("User") String username)
    {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        ApplicationUser current_user = ComponentAccessor.getUserManager().getUserByName(username);
        String key = current_user.getKey();
        boolean is_readonly = readOnlyUserService.isReadOnlyHardwareUser(key);
        JsonReadOnly resp;

        try{
            resp = generateResponseObject(is_readonly);
        }
        catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        return Response.ok(resp).build();
    }


    private JsonReadOnly generateResponseObject(boolean readonly) throws JSONException {
        JsonReadOnly resp = new JsonReadOnly(readonly);

        return resp;
    }

    @GET
    @Path("/getReadOnlyStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadOnlyStatus(@Context HttpServletRequest request)
    {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        ApplicationUser current_user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        boolean is_read_only_user = readOnlyUserService.isReadOnlyHardwareUser(current_user.getKey());
        boolean is_in_read_only_group = readOnlyHdwGroupService.isInReadOnlyGroup(current_user.getName());
        JsonReadOnly resp;

        try{
            resp = generateResponseObject((is_in_read_only_group || is_read_only_user));
        }
        catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        return Response.ok(resp).build();
    }

    @GET
    @Path("/getReadOnlyUsersAndGroups")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadOnlyUsersAndGroups(@Context HttpServletRequest request)
    {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        JsonReadOnly resp = new JsonReadOnly();
        resp.setReadOnlyGroups(readOnlyHdwGroupService.all());
        resp.setReadOnlyUsers(readOnlyUserService.all());

        return Response.ok(resp).build();
    }

    @POST
    @Path("/resetReadonlyUsersAndGroups")
    public Response resetReadOnlyUsersAndGroups(@Context HttpServletRequest request)
    {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        readOnlyHdwGroupService.clearReadOnlyHdwGroups();
        readOnlyUserService.clearReadOnlyHardwareUsers();

        return  Response.ok().build();
    }

    @PUT
    @Path("/saveReadOnlyUsersAndGroups")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveReadOnlyUsersAndGroups(@Context HttpServletRequest request, JsonReadOnly config)
    {
        Response unauthorized = checkHardwareRestPremission();
        if (unauthorized != null) {
            return unauthorized;
        }

        String error_response_text = "There was an error, processing your data!\n ";

        boolean result = true;
        boolean user_result = true;

        boolean users_present = config.getReadOnlyUsers().size() != 0;
        boolean groups_present = config.getReadOnlyGroups().size() != 0;

        if(!users_present && !groups_present)
            return Response.serverError().entity("The given Dataset is empty! \n " +
                    "Please check your entries and try again.").build();

        if(users_present) {
            readOnlyUserService.clearReadOnlyHardwareUsers();
            user_result = saveReadOnlyUsers(config.getReadOnlyUsers());
            result = user_result;
        }
        if(groups_present)
        {
            result &= saveReadOnlyGroups(config.getReadOnlyGroups());
        }

        if(!result)
        {
            if(!user_result)
                error_response_text += "One or more Usernames do not exist, please check your entries!";
            else
                error_response_text += "One or more UserGroups do not exist, please check your entries!";
            return Response.serverError().entity(error_response_text).build();
        }

        return Response.ok().build();
    }
}