package com.mt.system.fleet.entity.xinge_vo;

import java.util.List;

public class PushVo extends BaseXingeVo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Integer seq;
    private String push_id;
    private List<String> invalid_targe_list;
    private String environment;

    @Override
    public String toString() {
        super.toString();
        return "PushVo [seq=" + seq + ", push_id=" + push_id + ", invalid_targe_list=" + invalid_targe_list
            + ", environment=" + environment + "]";
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public List<String> getInvalid_targe_list() {
        return invalid_targe_list;
    }

    public void setInvalid_targe_list(List<String> invalid_targe_list) {
        this.invalid_targe_list = invalid_targe_list;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

}
