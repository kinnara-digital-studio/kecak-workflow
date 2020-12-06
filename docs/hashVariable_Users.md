### Users Hash Variable 
---

***Prefix***

```
Users
```

***Description***
```
To retrieve information of all the users in the selected group, grade, department and organization.
Multiple results will be separated by semicolon.
```

***Attributes***

```
#users.group.GROUP_ID.username#
#users.group.GROUP_ID.firstName#
#users.group.GROUP_ID.lastName#
#users.group.GROUP_ID.fullName#
#users.group.GROUP_ID.email#

#users.grade.GRADE_ID.username#
#users.grade.GRADE_ID.firstName#
#users.grade.GRADE_ID.lastName#
#users.grade.GRADE_ID.fullName#
#users.grade.GRADE_ID.email#

#users.department.DEPARTMENT_ID.username#
#users.department.DEPARTMENT_ID.firstName#
#users.department.DEPARTMENT_ID.lastName#
#users.department.DEPARTMENT_ID.fullName#
#users.department.DEPARTMENT_ID.email#

#users.organization.ORGANIZATION_ID.username#
#users.organization.ORGANIZATION_ID.firstName#
#users.organization.ORGANIZATION_ID.lastName#
#users.organization.ORGANIZATION_ID.fullName#
#users.organization.ORGANIZATION_ID.email
```

***Scope of Use***

-All Components within the App.

***Sample Attributes***

To return all the users in the current group id. 

```
#users.group.{currentUser.groups.id}.fullName#
#users.department.D-005.username#
#users.group.G-001.email#
```
