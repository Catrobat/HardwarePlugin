package org.catrobat.jira.adminhelper.rest.json;

import com.atlassian.plugin.PluginRegistry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by dominik on 27.12.16.
 */


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JsonReadOnly {

    @XmlElement
    boolean isReadOnly;
    @XmlElement
    List<String> readOnlyUsers;
    @XmlElement
    List<String> readOnlyGroups;

    public JsonReadOnly(){}

    public JsonReadOnly(boolean isReadOnly) {this.isReadOnly = isReadOnly;}

    public boolean getIsReadonly(){return this.isReadOnly;}

    public void setReadOnly(boolean readOnly){this.isReadOnly = readOnly;}

    public void setReadOnlyUsers(List<String> readOnlyUsers) {this.readOnlyUsers = readOnlyUsers; }

    public List<String> getReadOnlyUsers() { return this.readOnlyUsers; }

    public void setReadOnlyGroups(List<String> readOnlyGroups) {this.readOnlyGroups = readOnlyGroups; }

    public List<String> getReadOnlyGroups() {return this.readOnlyGroups; }
}
