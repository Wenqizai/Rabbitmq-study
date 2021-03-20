package com.rabbit.task.enums;

/**
 * @author Wenqi Liang
 * @date 2021/3/20
 * @desc
 */
public enum ElasticJobTypeEnum {

    SIMPLE("SimpleJob", "简单类型job"),
    DATAFLOW("DataflowJob", "流式类型job"),
    SCRIPT("ScriptJob", "脚本类型类型job");

    private String type;
    private String desc;

    ElasticJobTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}


