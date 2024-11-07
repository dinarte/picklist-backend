package com.quebecteh.modules.migrators.seervice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiRequestBuilder {


	 public static ObjectNode parseAndBuildJson(String inputText) {
	        ObjectMapper mapper = new ObjectMapper();
	        ObjectNode root = mapper.createObjectNode();

	        // Extrair o nome do objeto (primeira linha)
	        String name = extractName(inputText);
	        root.put("name", name);

	        // Extrair URL e HTTP Method
	        root.put("httpMethod", "POST"); // Assumindo POST com base no exemplo
	        root.put("url", extractUrl(inputText));

	        // Extrair headers
	        ObjectNode headersNode = root.putObject("headers");
	        headersNode.put("Authorization", "Zoho-oauthtoken {{token}}");
	        headersNode.put("content-type", "application/json");

	        // Extrair fields
	        ArrayNode fieldsArray = root.putArray("fields");
	        List<Field> fields = extractFields(inputText);
	        for (Field field : fields) {
	            fieldsArray.add(field.toJson(mapper));
	        }

	        return root;
	    }

	    private static String extractName(String text) {
	        return text.split("\n")[0].trim();
	    }

	    private static String extractUrl(String text) {
	        Pattern urlPattern = Pattern.compile("https://www\\.zohoapis\\.com/.+\\?organization_id=\\d+");
	        Matcher matcher = urlPattern.matcher(text);
	        if (matcher.find()) {
	            return matcher.group().replaceFirst("\\d+", "{{organization_id}}");
	        }
	        return null;
	    }

	    private static List<Field> extractFields(String text) {
	        List<Field> fields = new ArrayList<>();
	        Pattern fieldPattern = Pattern.compile("([a-zA-Z_]+)\\s+(string|integer|boolean|long|double|array|object)(?:\\s+\\(Required\\))?\\s*(.*)");

	        Matcher matcher = fieldPattern.matcher(text);
	        while (matcher.find()) {
	            String name = matcher.group(1).trim();
	            String type = matcher.group(2).trim();
	            boolean required = matcher.group(0).contains("(Required)");
	            String description = matcher.group(3).trim();

	            if ("array".equals(type) || "object".equals(type)) {
	                // Extrair subcampos e associar ao campo pai
	                List<Field> subFields = extractSubFields(text, matcher.end());
	                fields.add(new Field(name, type, required, description, subFields));
	            } else {
	                fields.add(new Field(name, type, required, description));
	            }
	        }

	        return fields;
	    }

	    private static List<Field> extractSubFields(String text, int startPos) {
	        List<Field> subFields = new ArrayList<>();
	        Pattern subFieldPattern = Pattern.compile("([a-zA-Z_]+)\\s+(string|integer|boolean|long|double)(?:\\s+\\(Required\\))?\\s*(.*)");

	        Matcher subMatcher = subFieldPattern.matcher(text);
	        subMatcher.region(startPos, text.length());

	        while (subMatcher.find()) {
	            String name = subMatcher.group(1).trim();
	            String type = subMatcher.group(2).trim();
	            boolean required = subMatcher.group(0).contains("(Required)");
	            String description = subMatcher.group(3).trim();

	            subFields.add(new Field(name, type, required, description));

	            // Parar ao encontrar um próximo campo principal fora do escopo do array/object atual
	            int nextLineStart = subMatcher.end();
	            Matcher nextFieldMatcher = Pattern.compile("^[a-zA-Z_]+\\s+(string|integer|boolean|long|double|array|object)").matcher(text);
	            nextFieldMatcher.region(nextLineStart, text.length());

	            if (nextFieldMatcher.find() && nextFieldMatcher.start() == nextLineStart) {
	                break;
	            }
	        }

	        return subFields;
	    }

	    public static class Field {
	        private String name;
	        private String type;
	        private boolean required;
	        private String description;
	        private List<Field> subFields;

	        public Field(String name, String type, boolean required, String description) {
	            this(name, type, required, description, null);
	        }

	        public Field(String name, String type, boolean required, String description, List<Field> subFields) {
	            this.name = name;
	            this.type = type;
	            this.required = required;
	            this.description = description;
	            this.subFields = subFields;
	        }

	        public ObjectNode toJson(ObjectMapper mapper) {
	            ObjectNode fieldNode = mapper.createObjectNode();
	            fieldNode.put("name", name);
	            fieldNode.put("type", type);
	            fieldNode.put("required", required);
	            fieldNode.put("description", description);

	            if (subFields != null && !subFields.isEmpty()) {
	                ArrayNode subFieldsArray = fieldNode.putArray("fields");
	                for (Field subField : subFields) {
	                    subFieldsArray.add(subField.toJson(mapper));
	                }
	            }

	            return fieldNode;
	        }
	    }

    public static void main(String[] args) {
        String inputText = "Create an Item Group\r\n"
        		+ "Create an item\r\n"
        		+ "curl --request POST \\\r\n"
        		+ "  --url 'https://www.zohoapis.com/inventory/v1/items?organization_id=10234695' \\\r\n"
        		+ "  --header 'Authorization: Zoho-oauthtoken 1000.41d9xxxxxxxxxxxxxxxxxxxxxxxxc2d1.8fccxxxxxxxxxxxxxxxxxxxxxxxx125f' \\\r\n"
        		+ "  --header 'content-type: application/json' \\\r\n"
        		+ "  --data '{\"field1\":\"value1\",\"field2\":\"value2\"}'\r\n"
        		+ "Arguments\r\n"
        		+ "group_id\r\n"
        		+ "string\r\n"
        		+ "Unique ID generated by the server for the group to which the item belongs, if any. This is used as an identifier.\r\n"
        		+ "group_name\r\n"
        		+ "string\r\n"
        		+ "Name of product group\r\n"
        		+ "unit\r\n"
        		+ "string\r\n"
        		+ "Unit of measurement for the item.\r\n"
        		+ "documents\r\n"
        		+ "array\r\n"
        		+ "List of all the documents associated with the item\r\n"
        		+ "item_type\r\n"
        		+ "string\r\n"
        		+ "Item type can be inventory, sales, purchases or sales_and_purchases. If item is associated with a group, then type should be inventory.\r\n"
        		+ "product_type\r\n"
        		+ "string\r\n"
        		+ "Type of the product. It can be goods or service\r\n"
        		+ "is_taxable\r\n"
        		+ "boolean\r\n"
        		+ "Boolean to track the taxability of the item.\r\n"
        		+ "tax_id\r\n"
        		+ "long\r\n"
        		+ "Unique ID generated by the server for the tax associated with the item. This is used a unique identifier.\r\n"
        		+ "description\r\n"
        		+ "string\r\n"
        		+ "Description of the Item.\r\n"
        		+ "purchase_account_id\r\n"
        		+ "long\r\n"
        		+ "Unique ID generated by the server for the Purchase account.\r\n"
        		+ "inventory_account_id\r\n"
        		+ "long\r\n"
        		+ "Uniquq ID generated by the server for the Inventory account.\r\n"
        		+ "attribute_name1\r\n"
        		+ "string\r\n"
        		+ "Name of the attribute present in the Item Group.\r\n"
        		+ "name\r\n"
        		+ "string\r\n"
        		+ "(Required)\r\n"
        		+ "Name of the Item.\r\n"
        		+ "rate\r\n"
        		+ "double\r\n"
        		+ "Sales price of the Item.\r\n"
        		+ "purchase_rate\r\n"
        		+ "double\r\n"
        		+ "Purchase price of the Item.\r\n"
        		+ "reorder_level\r\n"
        		+ "double\r\n"
        		+ "Reorder level of the item.\r\n"
        		+ "initial_stock\r\n"
        		+ "double\r\n"
        		+ "The opening stock of the item.\r\n"
        		+ "initial_stock_rate\r\n"
        		+ "double\r\n"
        		+ "The opening stock value of the item.\r\n"
        		+ "vendor_id\r\n"
        		+ "long\r\n"
        		+ "Unique ID generated by the server for the Vendor. This is used as an identifier.\r\n"
        		+ "vendor_name\r\n"
        		+ "string\r\n"
        		+ "Name of the preferred Vendor for purchasing this item.\r\n"
        		+ "sku\r\n"
        		+ "string\r\n"
        		+ "The Stock Keeeping Unit (SKU) of an item. This is unique for every item in the Inventory.\r\n"
        		+ "upc\r\n"
        		+ "long\r\n"
        		+ "The 12 digit Unique Product Code (UPC) of the item.\r\n"
        		+ "ean\r\n"
        		+ "long\r\n"
        		+ "Unique EAN value for the Item.\r\n"
        		+ "isbn\r\n"
        		+ "string\r\n"
        		+ "Unique ISBN value for the Item.\r\n"
        		+ "part_number\r\n"
        		+ "string\r\n"
        		+ "Part Number of the Item.\r\n"
        		+ "attribute_option_name1\r\n"
        		+ "long\r\n"
        		+ "Name of the attribute's option.\r\n"
        		+ "purchase_description\r\n"
        		+ "string\r\n"
        		+ "The description for the purchase information. This will be displayed to the vendor in your purchase order.\r\n"
        		+ "item_tax_preferences\r\n"
        		+ "array\r\n"
        		+ "🇮🇳 only\r\n"
        		+ "Hide Sub-Attributes arrow\r\n"
        		+ "tax_id\r\n"
        		+ "long\r\n"
        		+ "Unique ID generated by the server for the tax associated with the item. This is used a unique identifier.\r\n"
        		+ "tax_specification\r\n"
        		+ "string\r\n"
        		+ "🇮🇳 only\r\n"
        		+ "Type of tax. It can be intra or inter\r\n"
        		+ "hsn_or_sac\r\n"
        		+ "string\r\n"
        		+ "🇮🇳 only\r\n"
        		+ "HSN Code of the item\r\n"
        		+ "sat_item_key_code\r\n"
        		+ "string\r\n"
        		+ "🇲🇽 only\r\n"
        		+ "Add SAT Item Key Code for your goods/services. Download the CFDI Catalogs.\r\n"
        		+ "unitkey_code\r\n"
        		+ "string\r\n"
        		+ "🇲🇽 only\r\n"
        		+ "Add Unit Key Code for your goods/services. Download the CFDI Catalogs.\r\n"
        		+ "custom_fields\r\n"
        		+ "array\r\n"
        		+ "Custom fields for an item.\r\n"
        		+ "Hide Sub-Attributes arrow\r\n"
        		+ "customfield_id\r\n"
        		+ "long\r\n"
        		+ "value\r\n"
        		+ "string\r\n"
        		+ "Value of the Custom Field";

        ObjectNode jsonRequest = parseAndBuildJson(inputText);
        System.out.println(jsonRequest.toPrettyString());
    }
}
