# Heatmap Report

**Overview**

Heatmap Report Menu is userview menu plugin to display Heatmap Report. Heatmap Report visualize the “crowds” of activities in particular process, using either “Hit Count” or “Lead Time”.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist1.png" alt="plugins-datalist" />

**Pre-Configuration**

First of all you need to activate **Process Data Collector** in order to collect SLA data everytime Activity is finished. Plugin **Process Data Collector** is provided in Kecak Core.

## Setup Process Data Collector

1. Go to **Settings(1) > Properties & Export (2) > Set Plugin Default Properties (3)** 

![1](/up<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist1.png" alt="plugins-datalist" />
loads/cd62f1d3f435cdfcbf8983946003f5e4/1.png)

2. Navigate and select item **Process Data Collector**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist1.png" alt="plugins-datalist" />

3. A new pop-up will show, click **Submit**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist1.png" alt="plugins-datalist" />

**Plugin Properties**
*  Custom ID
*  Label

**Report Selection Parameters**

*  Process : Process Name
*  Report Type :
  *  Hit Count : Measurement are calculated based on number of activity’s execution count
  *  Lead Time : Measurement are calculated based on activity’s completions time

**Version History**

*  **1.0.0**
   * Initial creation
