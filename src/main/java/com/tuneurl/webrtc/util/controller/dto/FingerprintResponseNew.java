/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, Teodoro M. Albon, <albonteddy@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tuneurl.webrtc.util.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class FingerprintResponseNew {
  @JsonProperty("blockCount")
  private Integer blockCount;

  @JsonProperty("max")
  private Integer max;

  private List<FingerprintEntryItem> blockInfo;

  public FingerprintResponseNew() {}

  public Integer getBlockCount() {
    return blockCount;
  }

  public void setBlockCount(Integer blockCount) {
    this.blockCount = blockCount;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public List<FingerprintEntryItem> getBlockInfo() {
    return blockInfo;
  }

  public void setBlockInfo(List<FingerprintEntryItem> blockInfo) {
    this.blockInfo = blockInfo;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FingerprintData {\n");
    sb.append("    \"max\": ").append(getMax()).append(",\n");
    sb.append("    \"blockCount\": ").append(getBlockCount()).append(",\n");
    sb.append("    \"blockInfo\": [\n");
    for (FingerprintEntryItem block : blockInfo) {
      sb.append(block.toString()).append(",\n");
    }
    sb.append("    ]\n");
    sb.append("}\n");
    return sb.toString();
  }

  public String toJson() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();

    // StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("\"max\": ").append(getMax()).append(",\n");
    sb.append("\"blockCount\": ").append(getBlockCount()).append(",\n");
    sb.append("\"blockInfo\": [\n");
    for (FingerprintEntryItem block : blockInfo) {
      sb.append(block.toString()).append(",\n");
    }
    sb.append("]\n");
    sb.append("}");
    return sb.toString();
  }
}
