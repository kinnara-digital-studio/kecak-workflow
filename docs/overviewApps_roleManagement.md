# Role Management

Role enables administrators to manage management role groups; role assignment policies and management roles; and role entries, assignments, and scopes in an organization.

- **Authorization Object**
: attached to each field, user view or datalist, or element.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/authorizationObject.png" alt="authorizationObject" />
The image above is the image of the fill field for the authorization object, which is fill Object ID, Object Type (Field or Menu/Section), and Object Name.

The difference between Object Type Field and Menu / Section is, in Object Type Field must input Object Name, while in Object Type Menu / Section does not have to input Object Name.

The following is an example of data filling with Object Type in the form of Menu / Section:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement1.png" alt="roleManagement1" />
in Object Type Menu / Section does not have to input Object Name.

The following is an example of data filling with Object Type in the form of Field:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement2.png" alt="roleManagement2" />
in Object Type Field must input Object Name (not mandatory)

The following is an example of data filling with Object Type in the form of Action:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement3.png" alt="roleManagement3" />
in Object Type Field must input Object Name (not mandatory)


The data has been stored, then the data display is as follows (red marked):
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/rm_authObjectList.png" alt="rm_authObjectList" />


- **Role**
: specifies the authorization whether Read, Write or Hidden.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement5.png" alt="roleManagement5" />

The image above is the image of the fill field for the Role, which is fill Role ID and which Authorization Object into the Role.

The following is an example of data filling:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement6.png" alt="roleManagement6" />


The data has been stored, then the data display is as follows (red marked):
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/rm_roleList.png" alt="roleList" />

- **Role Group**
: attached to the person (user) user / group access

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/roleManagement8.png" alt="roleManagement8" />

The image above is the image of the fill field for the Role Group, which is fill Group ID, Roles, Users and Groups.

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


 Version History
*  **1.0.0**
   * Initial creation : Isti Fatimah
