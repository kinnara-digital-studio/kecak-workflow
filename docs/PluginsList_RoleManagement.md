# Role Management #

### Overview ###
Displays the role management settings


### How To Use ###
- **Authorization Object**
: attached to each field, user view or datalist, or element.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement1_fieldAddNew.png" alt="authorizationObject" />

Description :

|      FIELD      |         DESCRIPTION           |
|-----------------|-------------------------------|
|**Object ID**    |ID from authorization object   |
|**Object Type**  |Type from authorization object |


**Object Type**

* Field: paired on the permission
* Menu/Section: paired on the load binder
* Action: Trigger process/ execute button in data list action button (For Write)

The image above is the image of the fill field for the authorization object, which is filled Object ID, Object Type (Field or Menu/Section), and Object Name.

The difference between Object Type Field and Menu / Section is, in Object Type Field must input Object Name, while in Object Type Menu / Section does not have to input Object Name.

The following is an example of data filling with Object Type in the form of **Menu / Section**:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement2_addNew.png" alt="roleManagement_budget" />


The following is an example of data filling with Object Type in the form of **Actin** :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement3_addNew.png" alt="roleManagement2" />


The data has been stored, then the data display is as follows :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement4_list.png" alt="rm_authObjectList" />


- **Role**
: specifies the authorization whether Read, Write or Hidden.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement5.png" alt="roleManagement5" />

Description :

|           FIELD          |                               DESCRIPTION                              |
|--------------------------|------------------------------------------------------------------------|
|**Role ID**               |ID of the role to be created                                            |
|**Authorization Object**  |Authorization object which will be included in the role that is created |
|**Permission**            | Who can access the section                                             |


The image above is the image of the fill field for the Role, which is filled Role ID and which Authorization Object into the Role.

The following is an example of data filling:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement6.png" alt="roleManagement6" />


The data has been stored, then the data display is as follows (red marked):
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/rm_roleList.png" alt="roleList" />

- **Role Group**
: attached to the person (user) user/group access

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement8.png" alt="roleManagement8" />

Description :

|    FIELD    |                       DESCRIPTION                       |
|-------------|---------------------------------------------------------|
|**Group ID** |ID of the role group to be created                       |
|**Roles**    |Roles that will fit into the group to be created         |
|**Users**    |Determines which users are logged into the created group |
|**Groups**   |Groups in LDAP into the created group                    |


The image above is the image of the fill field for the Role Group, which is filled Group ID, Roles, Users, and Groups.

The following is an example of data filling:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_roleGroup.png" alt="roleManagement_roleGroup" />

The data has been stored, then the data display is as follows (red marked):

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleGroupList.png" alt="roleGroupList" />

- **LDAP**
: active directory. Employee database. Name, phone number username, address, etc. [RELATED WITH EMAIL]

example data from LDAP in Users when Field Role Group:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/rm_group_user.png" alt="group_user" />

example data from LDAP in Groups when Field Role Group:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/rm_group_groups.png" alt="group_groups" />

### Plugins Role Management Settings in Form/Section

1. Open the apps

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/.png" alt="" />


2. Choose form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_pluginsSet.png" alt="" />


3. Choose section

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_pluginsSetEditSection.png" alt="" />
 

4. Edit section in advanced option

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_AdvancePermission.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_AdvancePermissionChoose.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_AdvancePermissionChoose2.png" alt="" />

5. Permission

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_permission.png" alt="" />


6. Click OK

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_ok.png" alt="" />

7. Click Save

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement_save.png" alt="" />



**Version History**
*  **1.0.0**
   * Initial creation : Isti Fatimah
