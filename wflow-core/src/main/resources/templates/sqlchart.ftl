<!--[if lt IE 9]><script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/excanvas.js"></script><![endif]-->
<script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/jquery.jqplot.min.js"></script>
<script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.canvasTextRenderer.min.js"></script>
<script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>
<#if !element.properties.xAxisDisplayAS?? || element.properties.xAxisDisplayAS! == "">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.categoryAxisRenderer.js"></script>
</#if>
<#if element.properties.xAxisDisplayAS! == "date" || element.properties.chartType! == "candlestick" || element.properties.chartType! == "ohlc">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.dateAxisRenderer.js"></script>
</#if>
<#if element.properties.chartType! == "bar">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.barRenderer.min.js"></script>
<#elseif element.properties.chartType! == "pie">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.pieRenderer.min.js"></script>
<#elseif element.properties.chartType! == "donut">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.donutRenderer.min.js"></script>
<#elseif element.properties.chartType! == "bubble">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.bubbleRenderer.min.js"></script>
<#elseif element.properties.chartType! == "ohlc" || element.properties.chartType! == "candlestick">
    <script language="javascript" type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.ohlcRenderer.min.js"></script>
</#if>  
<#if element.properties.showValueLabel! == "true" && element.properties.chartType! == "bar">
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.pointLabels.js"></script>
</#if> 
<#if element.properties.showValueLabel! == "true" && element.properties.chartType! != "bar">
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.highlighter.min.js"></script>
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/plugins/jqplot.cursor.min.js"></script>
</#if> 
<link rel="stylesheet" type="text/css" href="${request.contextPath}/plugin/org.joget.plugin.enterprise.SqlChartMenu/jqplot/jquery.jqplot.css" />
<div class="chart-body-content">
    ${element.properties.customHeader!}

    <#if element.properties.error??>
        <div class="errors">
            ${element.properties.error!}
        </div>
    <#else>
        <script>
            $(document).ready(function(){
                var initChart = function() {
                    $("#jq_plot_chart").html("");
                    var data = ${data!};
                    jQuery.jqplot.config.enablePlugins = true; 
                    var plot1 = jQuery.jqplot ('jq_plot_chart', data, 
                        { 
                            title : "<h3>${element.properties.title!}<h3>",
                            ${seriesColors!}
                            <#if element.properties.showLegend! == "true">
                                legend: {
                                    ${element.properties.seriesLabel!}
                                    show:true, 
                                    location: 'e',
                                    placement: 'outsideGrid',
                                    rowSpacing: '0px'
                                },
                            </#if>    
                            <#if element.properties.stack! == "true" || element.properties.chartType! == "area">
                                stackSeries: true,
                            <#else>
                                stackSeries: false,
                            </#if>
                            <#if element.properties.showValueLabel! == "true" && element.properties.chartType! != "bar">
                                highlighter: {
                                    <#if element.properties.chartType! == "ohlc" || element.properties.chartType! == "candlestick">
                                        showMarker:false,
                                        tooltipAxes: 'xy',
                                        yvalues: 5,
                                        formatString:'<table class="jqplot-highlighter"><tr><td>@@userview.sqlchartmenu.ohlc.marker.date@@:</td><td>%s</td></tr><tr><td>@@userview.sqlchartmenu.ohlc.marker.open@@:</td><td>%s</td></tr><tr><td>@@userview.sqlchartmenu.ohlc.marker.hi@@:</td><td>%s</td></tr><tr><td>@@userview.sqlchartmenu.ohlc.marker.low@@:</td><td>%s</td></tr><tr><td>@@userview.sqlchartmenu.ohlc.marker.close@@:</td><td>%s</td></tr></table>'
                                    <#else>
                                        show: true,
                                        sizeAdjust: 7.5
                                    </#if>
                                },
                                cursor: {
                                    <#if element.properties.chartType! == "ohlc" || element.properties.chartType! == "candlestick">
                                        zoom:false,
                                        tooltipOffset: 10,
                                        tooltipLocation: 'nw'
                                    <#else>
                                        show: false
                                    </#if>
                                },
                            </#if>
                            <#if element.properties.chartType! != "xy">
                                seriesDefaults:{
                                    <#if element.properties.showPointLabel! == "true">
                                        pointLabels: { show: true, location: 'e'},
                                    </#if>
                                    <#if element.properties.chartType! == "bar">
                                        renderer:$.jqplot.BarRenderer,
                                        <#if element.properties.showValueLabel! == "true">
                                            pointLabels: {
                                                <#if element.properties.horizontal! == "true">
                                                    location: 'e', 
                                                    edgeTolerance: -15,
                                                </#if>
                                                show: true
                                            },
                                        </#if>
                                        <#if element.properties.horizontal! == "true">
                                            shadowAngle: 135,
                                        </#if>
                                        rendererOptions: {
                                            <#if element.properties.horizontal! == "true">
                                                barDirection: 'horizontal',
                                            </#if>
                                            fillToZero: true
                                        }
                                    <#elseif element.properties.chartType! == "pie">
                                        renderer: jQuery.jqplot.PieRenderer, 
                                        rendererOptions: {
                                            <#if element.properties.showValue! == "true">
                                                dataLabels: 'value',
                                            </#if>
                                            showDataLabels: true,
                                            sliceMargin: 3
                                        }
                                    <#elseif element.properties.chartType! == "donut">
                                        renderer: jQuery.jqplot.DonutRenderer, 
                                        rendererOptions: {
                                            <#if element.properties.showValue! == "true">
                                                dataLabels: 'value',
                                            </#if>
                                            showDataLabels: true,
                                            sliceMargin: 3
                                        }
                                    <#elseif element.properties.chartType! == "bubble">
                                        renderer: jQuery.jqplot.BubbleRenderer, 
                                        rendererOptions: {
                                            autoscalePointsFactor: -0.15,
                                            autoscaleMultiplier: 0.85,
                                            bubbleAlpha: 0.6,
                                            highlightAlpha: 0.8
                                        },
                                        shadow: true,
                                        shadowAlpha: 0.05
                                    <#elseif element.properties.chartType! == "ohlc" || element.properties.chartType! == "candlestick">
                                        <#if element.properties.chartType! == "candlestick">
                                                rendererOptions: {
                                                candleStick:true
                                            },
                                        </#if>        
                                        renderer: jQuery.jqplot.OHLCRenderer
                                    <#elseif element.properties.chartType! == "area">
                                        fill: true    
                                    <#elseif element.properties.chartType! == "line">    
                                        rendererOptions: {
                                            smooth: true
                                        }
                                    </#if>
                                },
                            </#if>
                            <#if element.properties.chartType! != "pie" && element.properties.chartType! != "donut">
                                axesDefaults: {
                                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer
                                },
                                axes: {
                                    <#if element.properties.horizontal! == "true">
                                        yaxis: {
                                            <#if (!element.properties.xAxisDisplayAS?? || element.properties.xAxisDisplayAS! == "") && !(element.properties.chartType! == "candlestick" || element.properties.chartType! == "ohlc") >
                                                renderer: $.jqplot.CategoryAxisRenderer,
                                                ticks: ${element.properties.ticks!},
                                            </#if>
                                            <#if element.properties.xAxisDisplayAS! == "date" || element.properties.chartType! == "candlestick" || element.properties.chartType! == "ohlc">
                                                renderer: $.jqplot.DateAxisRenderer,
                                                tickInset: 1,
                                                <#if element.properties.dateFormat?? && element.properties.dateFormat! != "">
                                                    tickOptions:{formatString:"${element.properties.dateFormat!}"},
                                                </#if>
                                            </#if>
                                            label: "${element.properties.categoryAxisLabel!}",
                                            pad: 0
                                        },
                                        xaxis: {
                                            <#if element.properties.yaxisPrefix?? && element.properties.yaxisPrefix! != "">
                                                tickOptions:{ prefix: '${element.properties.yaxisPrefix!}' },
                                            </#if>
                                            label: "${element.properties.valueAxisLabel!}"
                                        }
                                    <#elseif element.properties.chartType! == "bubble">
                                        xaxis: {
                                            label: "${element.properties.categoryAxisLabel!}"
                                        }, 
                                        yaxis: {
                                            label: "${element.properties.valueAxisLabel!}"
                                        }
                                    <#else>
                                        xaxis: {
                                            <#if (!element.properties.xAxisDisplayAS?? || element.properties.xAxisDisplayAS! == "") && !(element.properties.chartType! == "candlestick" || element.properties.chartType! == "ohlc") >
                                                renderer: $.jqplot.CategoryAxisRenderer,
                                                ticks: ${element.properties.ticks!},
                                            </#if>
                                            <#if element.properties.xAxisDisplayAS! == "date" || element.properties.chartType! == "candlestick" || element.properties.chartType! == "ohlc">
                                                renderer: $.jqplot.DateAxisRenderer,
                                                tickInset: 1,
                                                <#if element.properties.dateFormat?? && element.properties.dateFormat! != "">
                                                    tickOptions:{formatString:"${element.properties.dateFormat!}"},
                                                </#if>
                                            </#if>
                                            label: "${element.properties.categoryAxisLabel!}",
                                            pad: 0
                                        },
                                        yaxis: {
                                            <#if element.properties.yaxisPrefix?? && element.properties.yaxisPrefix! != "">
                                                tickOptions:{ prefix: '${element.properties.yaxisPrefix!}' },
                                            </#if>
                                            label: "${element.properties.valueAxisLabel!}"
                                        }
                                    </#if>
                                },
                            </#if>
                            animate: !$.jqplot.use_excanvas
                        }
                    );

                    <#if element.properties.chartType! == "candlestick" || element.properties.chartType! == "ohlc">
                        <#if element.properties.horizontal! == "true">
                            var axis = "yaxis";
                        <#else>
                            var axis = "xaxis";
                        </#if>

                        var min = plot1.axes[axis]._dataBounds.min;
                        var max = plot1.axes[axis]._dataBounds.max;
                        var pad = (max - min) / (data[0][0].length + 1);
                        plot1.axes[axis].min = new Date(min-pad);
                        plot1.axes[axis].max = new Date(max+pad);

                        plot1.replot();
                    </#if>

                    if ($("html").hasClass("ui-mobile")) {
                        $('div:jqmData(role="page")').off('pageshow',initChart);
                    }
                };

                //support jquery mobile
                if ($("html").hasClass("ui-mobile")) {
                    $('div:jqmData(role="page")').on('pageshow',initChart);
                } else {
                    initChart();
                }

                $(window).resize(function(){
                    initChart();
                });
            });
        </script>
    </#if>
    <style>
        #chart_container{width:${element.properties.chartWidth!};padding-bottom:${element.properties.chartHeight!};height: 0;margin:20px 0;}
        @media (max-width: 767px) {
            #chart_container{width:100%;padding-bottom:100%;}
        }
    </style>
    <div id="chart_container" style="position: relative;">
        <div id="jq_plot_chart" class="sqlChart" style="position: absolute; top: 0; left: 0;width: 100%;height: 100%;"></div>
    </div>
    ${element.properties.customFooter!}

</div>