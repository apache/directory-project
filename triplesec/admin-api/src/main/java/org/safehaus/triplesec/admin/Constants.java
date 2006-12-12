/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.admin;


import javax.naming.directory.ModificationItem;


public interface Constants
{
    String POLICY_PROFILE_OC = "policyProfile";
    String POLICY_PERMISSION_OC = "policyPermission";
    String POLICY_ROLE_OC = "policyRole";
    String SAFEHAUS_PROFILE_OC = "safehausProfile";
    String GROUP_OF_UNIQUE_NAMES_OC = "groupOfUniqueNames";
    String UID_OBJECT_OC = "uidObject";
    String EXTENSIBLE_OBJECT_OC = "extensibleObject";
    String ORGANIZATIONAL_PERSON_OC = "organizationalPerson";
    String PERSON_OC = "person";
    String INET_ORG_PERSON_OC = "inetOrgPerson";
    String KRB5PRINCIPAL_OC = "krb5Principal";
    String KRB5KDCENTRY_OC = "krb5KDCEntry";
    String REFERRAL_OC = "referral";
    
    String SAFEHAUS_RESYNCH_COUNT_ID = "safehausResynchCount";
    String SAFEHAUS_DISABLED_ID = "safehausDisabled";
    String KRB5_DISABLED_ID = "krb5AccountDisabled";
    String KRB5ENCRYPTION_TYPE_ID = "krb5EncryptionType";
    String KRB5KEY_VERSION_NUMBER_ID = "krb5KeyVersionNumber";
    String KRB5KEY_ID = "krb5Key";
    String KRB5PRINCIPAL_REALM_ID = "krb5PrincipalRealm";
    String KRB5PRINCIPAL_NAME_ID = "krb5PrincipalName";
    String APACHE_SAM_TYPE_ID = "apacheSamType";
    String STREET_ID = "street";
    String POSTAL_ADDRESS_ID = "postalAddress";
    String LOCALITY_NAME_ID = "l";
    String STATE_PROVINCE_ID = "st";
    String ZIP_POSTAL_CODE_ID = "postalCode";
    String COUNTRY_ID = "c";
    String ORGANIZATION_ID = "o";
    String COMMON_NAME_ID = "cn";
    String OBJECT_CLASS_ID = "objectClass";
    String UID_ID = "uid";
    String GIVENNAME_ID = "givenName";
    String SURNAME_ID = "sn";
    String PASSWORD_ID = "userPassword";
    String DESCRIPTION_ID = "description";
    String MOBILE_ID = "mobile";
    String EMAIL_ID = "mail";
    String NOTIFY_BY_ID = "safehausNotifyBy";
    String MOBILE_CARRIER_ID = "safehausMobileCarrier";
    String TOKEN_PIN_ID = "safehausTokenPin";
    String MIDLE_NAME_ID = "safehausMidletName";
    String FAILURES_IN_EPOCH_ID = "safehausFailuresInEpoch";
    String ACTIVATION_KEY_ID = "safehausActivationKey";
    String REALM_ID = "safehausRealm";
    String SECRET_ID = "safehausSecret";
    String LABEL_ID = "safehausLabel";
    String MOVING_FACTOR_ID = "safehausFactor";
    String UNIQUE_MEMBER_ID = "uniqueMember";
    String REF_ID = "ref";
    String GRANTS_ID = "grants";
    String DENIALS_ID = "denials";
    String ROLES_ID = "roles";
    String USER_ID = "user";
    String DOMAIN_COMPONENT_ID = "dc";
    String CREATE_TIMESTAMP_ID = "createTimestamp";
    String MODIFY_TIMESTAMP_ID = "modifyTimestamp";
    String MODIFIERS_NAME_ID = "modifiersName";
    String APP_NAME_ID = "appName";
    String PERM_NAME_ID = "permName";
    String ROLE_NAME_ID = "roleName";
    String PROFILEID_ID = "profileId";
    String SAFEHAUS_ID = "safehausUid";
    String CREATORS_NAME_ID = "creatorsName";

    ModificationItem[] EMPTY_MODS = new ModificationItem[0];
    String INVALID_MSG = "This modifier has persisted changes and is no longer valid.";
}
