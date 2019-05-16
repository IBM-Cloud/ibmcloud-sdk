package com.ibm.cloud.sdk.impl;

public class Namespace {

    private String id;
    private String location;
    private String name;
    private String description;
    private String resourceGroupId;
    private String resourcePlanId;
    private String classicSpaceGuid;
    private int classicType;

    public Namespace(String id, String location, String name, String description, String resourceGroupId, String resourcePlanId, String classicSpaceGuid, int classicType) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.description = description;
        this.resourceGroupId = resourceGroupId;
        this.resourcePlanId = resourcePlanId;
        this.classicSpaceGuid = classicSpaceGuid;
        this.classicType = classicType;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public String getResourcePlanId() {
        return resourcePlanId;
    }

    public String getClassicSpaceGuid() {
        return classicSpaceGuid;
    }

    public int getClassicType() {
        return classicType;
    }

    @Override
    public String toString() {
        return "Namespace [id=" + id + ", location=" + location + ", name=" + name + ", description=" + description + ", resourceGroupId=" + resourceGroupId + ", resourcePlanId="
                + resourcePlanId + ", classicSpaceGuid=" + classicSpaceGuid + ", classicType=" + classicType + "]";
    }

}
