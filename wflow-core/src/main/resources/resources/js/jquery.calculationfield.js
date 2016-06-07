(function($){
    
    $.fn.extend({
        calculationField : function(o){
            var target = this;
            if($(target)){
                if(o.readonly == undefined || o.readonly != 'true'){
                    if(!(o.variables == undefined || o.variables == null)){
                        $.each(o.variables, function(){
                            var variable = this;
                            var fieldId = variable.fieldId;
                            if(fieldId.indexOf(".") != -1){
                                fieldId = fieldId.substring(0, fieldId.indexOf("."));
                            }
                            var selector = "[name=" + fieldId + "]";
                            var field = $(selector);
                            if ($(field).length == 0) {
                                selector = "[name$=_" + fieldId + "]";
                                field = $();
                            }
                            
                            $(selector).addClass("control-field");
                            
                            var change = function() {
                                cfCalculation(target, o);
                            };
                            
                            $('body').off("change", selector, change);
                            $('body').on("change", selector, change);
                        });
                    }
                    // run on load 
                    $(document).ready(function() {
                        cfCalculation(target, o); 
                    });
                    // handle mobile page show
                    $(document).bind("pageshow", function() {
                        cfCalculation(target, o);
                    });
                }
            }
            return target;
        }
    });
    
    function cfCalculation(target, o){
        var equation = o.equation;
        if(o.equation == ""){
            equation = "0";
        }
        $.each(o.variables, function(){
            var variable = this;
            var value = getValue(variable.fieldId, variable.operation, o.format);
            if(o.equation == ""){
                equation += " + " + value;
            }else if(variable.variableName != ""){
                equation = replaceVariable(equation, variable.variableName, value);
            }
        });
        var result = eval(equation);
        var formattedResult  = formatResult(result, o);
        $(target).val(formattedResult);
        if ($(target).parent().find(".calculationField_value").length > 0) {
            $(target).parent().find(".calculationField_value").text(formattedResult);
        }
        $(target).trigger("change");
    };
    
    function getValue(fieldId, operation, format){
        if(format.toUpperCase() == "EURO"){
            $.Calculation.setDefaults({
                reNumbers: /(-?\s?\$?\s?)(\d+(\.\d{3})*(,\d{1,})?|,\d{1,})/g,
                cleanseNumber: function (v){
                    return v.replace(/[^0-9,\-]/g, "").replace(/,/g, ".");
                }
            });
        }else{
            $.Calculation.setDefaults({
                reNumbers: /(-?\s?\$?\s?)(\d+(,\d{3})*(\.\d{1,})?|\.\d{1,})/g,
                cleanseNumber: function (v){
                    return v.replace(/[^0-9.\-]/g, "");
                }
            });
        }
        
        var value = 0;
        
        if (fieldId.indexOf(".") != -1) {
            selector = FormUtil.getGridCells(fieldId);
        } else {
            selector = FormUtil.getField(fieldId);
            
            //fix for IE (Grid values should not added even its have same id)
            selector = $(selector).filter(":not(.grid-cell)");
        }
        
        //if radio or checkbox, count oni checked
        var type = $(selector).attr('type');
        if (type != undefined && (type.toLowerCase() == "radio" || type.toLowerCase() == "checkbox")) {
           selector = $(selector).filter(":checked");
        }
        if(operation.toUpperCase() == "AVG"){
            value = $(selector).avg();
        }else if(operation.toUpperCase() == "MIN"){
            value = $(selector).min();
        }else if(operation.toUpperCase() == "MAX"){
            value = $(selector).max();
        }else{
            value = $(selector).sum();
        }
        
        if (!isFinite(value)) {
            value = 0;
        }
        
        return value;
    }
    
    function replaceVariable(equation, variableName, value){
        var match = new RegExp(variableName, "ig"); 
        return equation.replace(match, value);
    }
    
    function formatResult(result, o){
        var numOfDecimal = parseInt(o.numOfDecimal);
        var decimalSeperator = ".";
        var thousandSeparator = ",";
        if(o.format.toUpperCase() == "EURO"){
            decimalSeperator = ",";
            thousandSeparator = ".";
        }
        
        var number = result;
        var exponent = "";
        if (!isFinite(number)) {
            number = 0;
        } else {
            var numberstr = result.toString();
            var eindex = numberstr.indexOf("e");
            if (eindex > -1){
                exponent = numberstr.substring(eindex);
                number = parseFloat(numberstr.substring(0, eindex));
            }

            if (numOfDecimal != null){
                var temp = Math.pow(10, numOfDecimal);
                number = Math.round(number * temp) / temp;
            }
        }
        
        var sign = number < 0 ? "-" : "";
        
        var integer = (number > 0 ? Math.floor (number) : Math.abs (Math.ceil (number))).toString ();
        var fractional = number.toString ().substring (integer.length + sign.length);
        fractional = numOfDecimal != null && numOfDecimal > 0 || fractional.length > 1 ? (decimalSeperator + fractional.substring (1)) : "";
        if(numOfDecimal != null && numOfDecimal > 0){
            for (i = fractional.length - 1, z = numOfDecimal; i < z; ++i){
                fractional += "0";
            }
        }
        
        if(o.useThousandSeparator.toUpperCase() == "TRUE"){
            for (i = integer.length - 3; i > 0; i -= 3){
                integer = integer.substring (0 , i) + thousandSeparator + integer.substring (i);
            }
        }
        
        var resultString = "";
        if(sign != ""){
            resultString += sign;
        }
        if(o.prefix != ""){
            resultString += o.prefix + ' ';
        }
        resultString += integer + fractional;
        if(exponent != ""){
            resultString += ' ' + exponent;
        }
        if(o.postfix != ""){
            resultString += ' ' + o.postfix;
        }
        
        return  resultString;
    }
})(jQuery);