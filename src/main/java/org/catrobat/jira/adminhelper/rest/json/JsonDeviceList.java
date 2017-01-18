package org.catrobat.jira.adminhelper.rest.json;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by dominik on 18.01.17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class JsonDeviceList {

    @XmlElement
    private List<JsonDevice> device_list;

    public JsonDeviceList(List<JsonDevice> device_list)
    {
        this.device_list = device_list;
    }

    public List<JsonDevice> getDeviceList()
    {
        return this.device_list;
    }
}
