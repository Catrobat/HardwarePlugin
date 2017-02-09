package org.catrobat.jira.adminhelper.helper;

import com.atlassian.jira.bc.issue.comment.CommentService;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.util.UserManager;
import com.google.gson.Gson;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.rest.json.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dominik on 20.01.17.
 */
public class JSONImporter {

    private final UserManager userManager;
    private final DeviceService deviceService;
    private final HardwareModelService hardwareModelService;
    private final LendingService lendingService;
    private final DeviceCommentService deviceCommentService;
    private Map<Integer, Integer> hardware_model_mapping;
    private final AdminHelperConfigService configService;

    public JSONImporter(DeviceService deviceService, UserManager userManager, HardwareModelService hardwareModelService,
                        LendingService lendingService, DeviceCommentService deviceCommentService,
                        AdminHelperConfigService configService)
    {
        this.deviceService = deviceService;
        this.userManager = userManager;
        this.hardwareModelService = hardwareModelService;
        this.lendingService = lendingService;
        this.deviceCommentService = deviceCommentService;
        this.hardware_model_mapping = new HashMap<>();
        this.configService = configService;
    }

    public boolean ImportDevices(JsonDeviceList deviceList)
    {
        System.out.println("Importing Devices");
        Gson gson = new Gson();
        for(JsonDevice d : deviceList.getDeviceList()) {
            System.out.println("importing Device:");
            System.out.println(gson.toJson(d));
            HardwareModel hd;
            if(!hardware_model_mapping.containsKey(d.getHardwareModelId())) {
                System.out.println("Hardware Model does not exist, about to create new model");
                JsonHardwareModel json_hardware = d.getHardwareModel();
                hd = hardwareModelService.add(json_hardware.getName(),json_hardware.getTypeOfDevice(),
                        json_hardware.getVersion(), json_hardware.getPrice(),json_hardware.getProducer(),
                        json_hardware.getOperatingSystem(),json_hardware.getArticleNumber());
                System.out.println(hd.getID());
                hardware_model_mapping.put(d.getHardwareModelId(), hd.getID());
            }
            else {
                hd = hardwareModelService.get(hardware_model_mapping.get(d.getHardwareModelId()));
            }
            if(d.getId() == -1) {
                System.out.println("only model without devices found, continue");
                continue;
            }

            Device current_device = deviceService.add(hd,d.getImei(), d.getSerialNumber(), d.getInventoryNumber(),
                    d.getReceivedFrom(), d.getReceivedDate(),d.getUsefulLiveOfAsset());

            if(d.getSortedOutDate() != null) {
                System.out.println("device has been sorted out");
                deviceService.sortOutDevice(current_device.getID(), d.getSortedOutDate(), d.getSortedOutComment());
            }

            List<JsonLending> lendings = d.getLendings();
            List<JsonDeviceComment> comments = d.getComments();

            for(JsonLending lending : lendings) {
                Lending temp = lendingService.lendOut(current_device,lending.getLentOutBy(), lending.getLentOutIssuer(),
                        lending.getPurpose(), lending.getComment(), lending.getBegin());
                if(lending.getEnd() != null) {
                    lendingService.bringBack(temp, lending.getPurpose(), lending.getComment(), lending.getEnd());
                }
            }

            for(JsonDeviceComment comment : comments) {
                if(comment.getDate() != null) {
                    deviceCommentService.addDeviceComment(current_device, comment.getAuthor(), comment.getComment(),
                            comment.getDate());
                }
                else {
                     deviceCommentService.addDeviceComment(current_device, comment.getAuthor(),
                            comment.getComment());
                }
            }
        }
        System.out.println("Imported all Devices");
        hardware_model_mapping.clear();
        return true;
    }

    public boolean importConfig(JsonConfig config)
    {
        System.out.println("importing config");
        try {
            HelperUtil.saveGithubConfig(config, configService);
            HelperUtil.saveConfig(config, configService);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}
