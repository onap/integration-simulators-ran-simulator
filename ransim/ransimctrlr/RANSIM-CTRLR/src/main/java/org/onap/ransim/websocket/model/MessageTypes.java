/*-
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.websocket.model;

public class MessageTypes {
    public final static String RC_TO_HC_FMDATA = "FmData";
    public final static String RC_TO_HC_PMDATA = "PmData";
    public final static String RC_TO_HC_SETCONFIGTOPO = "INITIAL_CONFIG";
    public final static String RC_TO_HC_UPDCELL = "UpdateCell";
    public final static String RC_TO_HC_PING = "PING";
    public final static String RC_TO_HC_PMFILEDATA = "RC_TO_HC_PMFILEDATA";
    public final static String HC_TO_RC_MODPCI = "ModifyPci";
    public final static String HC_TO_RC_MODANR = "ModifyAnr";
    public final static String HC_TO_RC_RTRIC = "RTRIC_CONFIG";
    public final static String HC_TO_RC_RRM_POLICY = "HC_TO_RC_RRM_POLICY";
    public final static String HC_TO_RC_PLMN = "HC_TO_RC_PLMN";
    public final static String HC_TO_RC_SLICE_PROFILE = "HC_TO_RC_SLICE_PROFILE";
    public final static String HC_TO_RC_RRM_POLICY_DEL = "HC_TO_RC_RRM_POLICY_DEL";
    public final static String HC_TO_RC_PLMN_DEL = "HC_TO_RC_PLMN_DEL";
    public final static String HC_TO_RC_SLICE_PROFILE_DEL = "HC_TO_RC_SLICE_PROFILE_DEL";
}
