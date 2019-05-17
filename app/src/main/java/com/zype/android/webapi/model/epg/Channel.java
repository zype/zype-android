package com.zype.android.webapi.model.epg;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channel implements Serializable {
  @SerializedName("_id")
  @Expose
  public String id;

  @SerializedName("name")
  @Expose
  public String name;

  @SerializedName("status")
  @Expose
  public String status;

  @SerializedName("program_guide_entry_count")
  public int programGuideEntryCount;
  private List<Program> programs = new ArrayList<>();

  public boolean isActive() {
   /* if (!TextUtils.isEmpty(status)) {
      return status.equalsIgnoreCase("synced");
    }*/

    return true;
  }

  public List<Program> getPrograms() {
    return programs;
  }

  public void setPrograms(List<Program> programs) {
    this.programs = programs;
  }

  public void addProgram(List<Program> programs) {

    for (Program program : programs) {
      this.programs.add(program);
    }
  }
}
