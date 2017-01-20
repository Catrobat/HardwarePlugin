package org.catrobat.jira.adminhelper.helper;

import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.Device;
import org.catrobat.jira.adminhelper.activeobject.DeviceService;
import org.catrobat.jira.adminhelper.activeobject.HardwareModel;
import org.catrobat.jira.adminhelper.activeobject.HardwareModelService;
import org.catrobat.jira.adminhelper.rest.json.JsonDevice;
import org.catrobat.jira.adminhelper.rest.json.JsonDeviceList;
import org.catrobat.jira.adminhelper.rest.json.JsonHardwareModel;

/**
 * Created by dominik on 20.01.17.
 */
public class JSONImporter {

    private final UserManager userManager;
    private final DeviceService deviceService;
    private final HardwareModelService hardwareModelService;

    public JSONImporter(DeviceService deviceService, UserManager userManager, HardwareModelService hardwareModelService)
    {
        this.deviceService = deviceService;
        this.userManager = userManager;
        this.hardwareModelService = hardwareModelService;
    }

    public boolean ImportDevices(JsonDeviceList deviceList)
    {
        System.out.println("Importing Devices");
        for(JsonDevice d : deviceList.getDeviceList())
        {
            HardwareModel hd = hardwareModelService.get(d.getHardwareModelId());
            if(hd == null) {
                System.out.println("Hardware Model does not exist, about to create new model");
                JsonHardwareModel json_hardware = d.getHardwareModel();
                hd = hardwareModelService.add(json_hardware.getName(),json_hardware.getTypeOfDevice(),
                        json_hardware.getVersion(), json_hardware.getPrice(),json_hardware.getProducer(),
                        json_hardware.getOperatingSystem(),json_hardware.getArticleNumber());
            }
            deviceService.add(hd,d.getImei(), d.getSerialNumber(), d.getInventoryNumber(),
                    d.getReceivedFrom(), d.getReceivedDate(),d.getUsefulLiveOfAsset());
        }
        System.out.println("Imported all Devices");
        return true;
    }
}
