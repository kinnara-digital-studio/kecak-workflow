###Form Binder Hash Variabel###

---

***Prefix***

```
binder
```
***Description***

```
To get `field value` or `field value label` from the binder of a form via its form `definition id` .

The `binder` prefix retrieves the field value, while the `binder.options` prefix retrieves the lookup field (eg. select box, checkbox & radio button) option label.

The `field value` or `field value label` can also be obtained from other apps by including the APP_ID, else it uses the current app context.

By providing the `PRIMARY_KEY` argument, `field value` or `field value label` from a different record can be retrieved, else data from the current record id is retrieved instead.

***Attributes***

```
1. #binder.APP_ID.FORM_DEF_ID.FIELD_ID#
2. #binder.APP_ID.FORM_DEF_ID.FIELD_ID[PRIMARY_KEY]#
3. #binder.FORM_DEF_ID.FIELD_ID#
4. #binder.FORM_DEF_ID.FIELD_ID[PRIMARY_KEY]#
5. #binder.options.APP_ID.FORM_DEF_ID.FIELD_ID#
6. #binder.options.APP_ID.FORM_DEF_ID.FIELD_ID[PRIMARY_KEY]#
7. #binder.options.FORM_DEF_ID.FIELD_ID#
8. #binder.options.FORM_DEF_ID.FIELD_ID[PRIMARY_KEY]#
```

***Scope of Use***

- In a process Tool a part of a Prosses.
- In a Form.
- In a process design.

***Sample Attribute***

```
#binder.addCard.title#

#binder.cardViewer.addCard.title#

#binder.cardViewer.addCard.title[b30bce20-c0a82095-14976e70-fded1735]#

#binder.options.addCard.title#

#binder.options.cardViewer.addCard.title#
```

#binder.options.cardViewer.addCard.title[b30bce20-c0a82095-14976e70-fded1735]#