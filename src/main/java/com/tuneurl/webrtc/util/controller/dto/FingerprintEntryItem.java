package com.tuneurl.webrtc.util.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FingerprintEntryItem {
  /**
   * The block size.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setBlockSize(), getBlockSize().
   */
  @JsonProperty("blockSize")
  private Integer blockSize;

  /**
   * The sum.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setSum(), getSum().
   */
  @JsonProperty("sum")
  private Integer sum;

  /**
   * The average.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setAverage(), getAverage().
   */
  @JsonProperty("average")
  private Double average;

  /**
   * The start rate.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setStartRate(), getStartRate().
   */
  @JsonProperty("startRate")
  private Double startRate;

  /**
   * The end rate.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setEndRate(), getEndRate().
   */
  @JsonProperty("endRate")
  private Double endRate;

  /** Default constructor. */
  public FingerprintEntryItem() {}

  /**
   * Retrieves the block size.
   *
   * @return the block size
   */
  public Integer getBlockSize() {
    return blockSize;
  }

  /**
   * Sets the block size.
   *
   * @param blockSize the block size to set
   */
  public void setBlockSize(Integer blockSize) {
    this.blockSize = blockSize;
  }

  /**
   * Retrieves the sum.
   *
   * @return the sum
   */
  public Integer getSum() {
    return sum;
  }

  /**
   * Sets the sum.
   *
   * @param sum the sum to set
   */
  public void setSum(Integer sum) {
    this.sum = sum;
  }

  /**
   * Retrieves the average.
   *
   * @return the average
   */
  public Double getAverage() {
    return average;
  }

  /**
   * Sets the average.
   *
   * @param average the average to set
   */
  public void setAverage(Double average) {
    this.average = average;
  }

  /**
   * Retrieves the start rate.
   *
   * @return the start rate
   */
  public Double getStartRate() {
    return startRate;
  }

  /**
   * Sets the start rate.
   *
   * @param startRate the start rate to set
   */
  public void setStartRate(Double startRate) {
    this.startRate = startRate;
  }

  /**
   * Retrieves the end rate.
   *
   * @return the end rate
   */
  public Double getEndRate() {
    return endRate;
  }

  /**
   * Sets the end rate.
   *
   * @param endRate the end rate to set
   */
  public void setEndRate(Double endRate) {
    this.endRate = endRate;
  }

  /**
   * To String.
   *
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataBlock {\n");
    sb.append("    \"blockSize\": ").append(getBlockSize()).append(",\n");
    sb.append("    \"sum\": ").append(getSum()).append(",\n");
    sb.append("    \"average\": ").append(getAverage()).append(",\n");
    sb.append("    \"startRate\": ").append(getStartRate()).append(",\n");
    sb.append("    \"endRate\": ").append(getEndRate()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
