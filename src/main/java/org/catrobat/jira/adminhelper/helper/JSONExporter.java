package org.catrobat.jira.adminhelper.helper;

import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.json.JSONObject;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.Device;
import org.catrobat.jira.adminhelper.activeobject.DeviceService;
import org.catrobat.jira.adminhelper.rest.json.JsonConfig;
import org.catrobat.jira.adminhelper.rest.json.JsonDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 18.01.17.
 */
public class JSONExporter {

    private final DeviceService deviceService;
    private final UserManager userManager;
    private final AdminHelperConfigService configService;

    public JSONExporter(DeviceService deviceService, UserManager userManager, AdminHelperConfigService configService) {
        System.out.println("constructing JSON exporter");
        this.deviceService = deviceService;
        this.userManager = userManager;
        this.configService = configService;
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

        return json_list;
    }

    public JsonConfig getConfig()
    {
        return new JsonConfig(configService);
    }
}
