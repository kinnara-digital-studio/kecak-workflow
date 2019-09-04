# Formatter Hash Variable #

### Plugin Type ###
Hash Variable

### Overview ###
Basic usage format : `#formatter.[type].[parameters]#`
*  *type* : **optionsLabel** | **convertCase**
*  *parameters* : May vary based on *type*, read below for details

#### Options Label ####
Format value using Options Binder label if available

Usage:

`#formatter.optionsLabel.[formDefId].[field].[value]#`
*  *formDefId* : Form ID
*  *field* : Field ID
*  *value* : Value that need to be converted

Example:

`#formatter.optionsLabel.eappr_start.approver.{form.eappr_t_spd.approver}#`

#### Convert Case ####
Convert value's case

Usage:

`#formatter.convertCase.lower|upper.[value]#`

Example:

`#formatter.convertCase.lower.HELLO#`

`#formatter.convertCase.upper.heLLo#`

#### String Escape ####

Escape string value. Available methods are  unescapeCsv, escapeCsv, escapeXml, escapeJavaScript, unescapeJavaScript, escapeHtml, escapeSql, escapeJava, unescapeHtml, unescapeXml, unescapeJava

Usage:

`#formatter.stringEscape.[method].[value]#`

Example:

`#formatter.stringEscape.escapeHtml.<span>myhtml</span>#`

#### Formatter Grid HTML ####
Convert data from table in web so can view in email report.

Usage:

`#formatter.gridHtmlTable.[formDefId].[gridField]#`

Example:

`#formatter.gridHtmlTable.spd_create_new.additional_participant#`

this hash variable is convert from table to text html.
example scrpt HTML:
```javascript
<table id="m_-3932008519465551303spd_new_spd.additional_requester" class="m_-3932008519465551303gridtable-tab" width="100%">
<tbody>
 <tr class="m_-3932008519465551303gridtable-head-row">
  <th class="m_-3932008519465551303gridtable-head-col" width="50%">Nama</th><th class="m_-3932008519465551303" width="50%">Jumlah</th>
 </tr>
 <tr class="m_-3932008519465551303gridtable-body-row">
  <td class="m_-3932008519465551303gridtable-body-col" align="center" width="50%">Emil Ermindra</td>
  <td class="m_-3932008519465551303gridtable-body-col" align="center">12868421.00</td>
 </tr>
</tbody>
</table>
```

Example output in Email notification:

#### In Mobile Apps ####
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/formatterHashVariable1.png" alt="formatterHashVariable1.png" />

#### In Website ####
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/formatterHashVariable2.png" alt="formatterHashVariable2.png" />

### Version History ###

*  **1.0.0**
   * Initial creation
   * Isti Fatimah
