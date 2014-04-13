package ru.server.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "video")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Video {
  private int id;

  public Video() {
    this(0);
  }

  public Video(int id) {
    this.id = id;
  }

  @XmlAttribute(name = "id") @JsonProperty(value = "ID")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

}