package org.catrobat.jira.adminhelper.helper;

import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.json.JSONObject;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.rest.json.JsonConfig;
import org.catrobat.jira.adminhelper.rest.json.JsonDevice;
import org.catrobat.jira.adminhelper.rest.json.JsonHardwareModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 18.01.17.
 */
public class JSONExporter {

    private final DeviceService deviceService;
    private final UserManager userManager;
    private final AdminHelperConfigService configService;
    private final HardwareModelService hardwareModelService;

    public JSONExporter(DeviceService deviceService, UserManager userManager, AdminHelperConfigService configService,
                        HardwareModelService hardwareModelService) {
        System.out.println("constructing JSON exporter");
        this.deviceService = deviceService;
        this.userManager = userManager;
        this.configService = configService;
        this.hardwareModelService = hardwareModelService;
    }

    public List<JsonDevice> getDevicesAsJSON()
    {
        List<Device> device_list = deviceService.all();
        List<JsonDevice> json_list = new ArrayList<>();

        for(Device d : device_list)
        {
            JsonDevice current = new JsonDevice(d, userManager);
            json_list.add(current);
        }

        List<HardwareModel> hardware_models = hardwareModelService.all();
        for(HardwareModel model : hardware_models){
            if(model.getDevices().length == 0)
            {
                JsonDevice only_model = new JsonDevice(new JsonHardwareModel(model));
                only_model.setId(-1);
                only_model.setHardwareModelId(model.getID());
                json_list.add(only_model);
            }
        }

        return json_list;
    }

    public JsonConfig getConfig()
    {
        return new JsonConfig(configService);
    }
}
