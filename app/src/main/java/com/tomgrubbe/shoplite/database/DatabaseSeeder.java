package com.tomgrubbe.shoplite.database;


import com.tomgrubbe.shoplite.model.Category;
import com.tomgrubbe.shoplite.model.Product;

public class DatabaseSeeder {

    public static void SeedInitialDatabase(ProductsTable products, CategoriesTable categories)   {

        // Add default Categories
        categories.addCategory(new Category(null, Category.UNCATEGORIZED));
        categories.addCategory(new Category(null, "Produce"));
        categories.addCategory(new Category(null, "Dairy"));
        categories.addCategory(new Category(null, "Canned/Jarred"));
        categories.addCategory(new Category(null, "Deli"));
        categories.addCategory(new Category(null, "Meats"));
        categories.addCategory(new Category(null, "Household"));
        categories.addCategory(new Category(null, "Snacks"));
        categories.addCategory(new Category(null, "Cleaning"));
        categories.addCategory(new Category(null, "Personal Care"));
        categories.addCategory(new Category(null, "Bakery"));
        categories.addCategory(new Category(null, "Baking/Cooking"));
        categories.addCategory(new Category(null, "Seasoning"));
        categories.addCategory(new Category(null, "Frozen Foods"));
        categories.addCategory(new Category(null, "Candy"));
        categories.addCategory(new Category(null, "Spices"));
        categories.addCategory(new Category(null, "Coffee/Tea"));
        categories.addCategory(new Category(null, "Beverages"));
        categories.addCategory(new Category(null, "Medications"));
        categories.addCategory(new Category(null, "Condiments"));

        // TODO: Add More!


        // Add default Products
        products.addProduct(new Product(null, "Grapes", categories.findId("Produce")));
        products.addProduct(new Product(null, "Tomatoes", categories.findId("Produce")));
        products.addProduct(new Product(null, "Apples", categories.findId("Produce")));
        products.addProduct(new Product(null, "Lettuce", categories.findId("Produce")));
        products.addProduct(new Product(null, "Celery", categories.findId("Produce")));
        products.addProduct(new Product(null, "Potatoes", categories.findId("Produce")));
        products.addProduct(new Product(null, "Onions", categories.findId("Produce")));

        products.addProduct(new Product(null, "Butter", categories.findId("Dairy")));
        products.addProduct(new Product(null, "Eggs", categories.findId("Dairy")));
        products.addProduct(new Product(null, "Cheese", categories.findId("Dairy")));
        products.addProduct(new Product(null, "Bacon", categories.findId("Dairy")));
        products.addProduct(new Product(null, "Milk", categories.findId("Dairy")));
        products.addProduct(new Product(null, "Orange Juice", categories.findId("Dairy")));

        products.addProduct(new Product(null, "Soup", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Tomato Sauce", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Tomato Paste", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Apple Sauce", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Corn (canned)", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Green Beans (canned)", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Pickles", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Olives", categories.findId("Canned/Jarred")));
        products.addProduct(new Product(null, "Baked Beans", categories.findId("Canned/Jarred")));

        products.addProduct(new Product(null, "Cole Slaw", categories.findId("Deli")));
        products.addProduct(new Product(null, "Lunch Meat", categories.findId("Deli")));
        products.addProduct(new Product(null, "Potato Salad", categories.findId("Deli")));

        products.addProduct(new Product(null, "Steak", categories.findId("Meats")));
        products.addProduct(new Product(null, "Pork Chops", categories.findId("Meats")));
        products.addProduct(new Product(null, "Chicken", categories.findId("Meats")));
        products.addProduct(new Product(null, "Roast", categories.findId("Meats")));
        products.addProduct(new Product(null, "Filet Mignon", categories.findId("Meats")));
        products.addProduct(new Product(null, "Fish", categories.findId("Meats")));

        products.addProduct(new Product(null, "Garbage Bags", categories.findId("Household")));
        products.addProduct(new Product(null, "Bug Spray", categories.findId("Household")));
        products.addProduct(new Product(null, "Paper Towels", categories.findId("Household")));
        products.addProduct(new Product(null, "Toilet Paper", categories.findId("Household")));
        products.addProduct(new Product(null, "Light Bulbs", categories.findId("Household")));
        products.addProduct(new Product(null, "Freezer Bags", categories.findId("Household")));
        products.addProduct(new Product(null, "Sandwich Bags", categories.findId("Household")));

        products.addProduct(new Product(null, "Tortilla Chips", categories.findId("Snacks")));
        products.addProduct(new Product(null, "Potato Chips", categories.findId("Snacks")));
        products.addProduct(new Product(null, "Peanuts", categories.findId("Snacks")));
        products.addProduct(new Product(null, "Beef Jerky", categories.findId("Snacks")));
        products.addProduct(new Product(null, "Cashews", categories.findId("Snacks")));

        products.addProduct(new Product(null, "Dish Soap", categories.findId("Cleaning")));
        products.addProduct(new Product(null, "Floor Cleaner", categories.findId("Cleaning")));
        products.addProduct(new Product(null, "Sponges", categories.findId("Cleaning")));
        products.addProduct(new Product(null, "Laundry Detergent", categories.findId("Cleaning")));

        products.addProduct(new Product(null, "Hand Soap", categories.findId("Personal Care")));
        products.addProduct(new Product(null, "Shampoo", categories.findId("Personal Care")));
        products.addProduct(new Product(null, "Hair Gel", categories.findId("Personal Care")));
        products.addProduct(new Product(null, "Hair Spray", categories.findId("Personal Care")));
        products.addProduct(new Product(null, "Body Lotion", categories.findId("Personal Care")));
        products.addProduct(new Product(null, "Baby Oil", categories.findId("Personal Care")));

        products.addProduct(new Product(null, "Bread", categories.findId("Bakery")));
        products.addProduct(new Product(null, "Pie", categories.findId("Bakery")));
        products.addProduct(new Product(null, "Cake", categories.findId("Bakery")));

        products.addProduct(new Product(null, "Flour", categories.findId("Baking/Cooking")));
        products.addProduct(new Product(null, "Vegetable Oil", categories.findId("Baking/Cooking")));
        products.addProduct(new Product(null, "Sugar", categories.findId("Baking/Cooking")));

        products.addProduct(new Product(null, "Mixed Vegetables", categories.findId("Frozen Foods")));
        products.addProduct(new Product(null, "Vegetables", categories.findId("Frozen Foods")));
        products.addProduct(new Product(null, "Ice Cream", categories.findId("Frozen Foods")));

        products.addProduct(new Product(null, "Candy", categories.findId("Candy")));
        products.addProduct(new Product(null, "Gum", categories.findId("Candy")));
        products.addProduct(new Product(null, "Chocolate", categories.findId("Candy")));

        products.addProduct(new Product(null, "Salt", categories.findId("Spices")));
        products.addProduct(new Product(null, "Pepper", categories.findId("Spices")));
        products.addProduct(new Product(null, "Oregano", categories.findId("Spices")));
        products.addProduct(new Product(null, "Paprika", categories.findId("Spices")));
        products.addProduct(new Product(null, "Rosemary", categories.findId("Spices")));
        products.addProduct(new Product(null, "Sage", categories.findId("Spices")));
        products.addProduct(new Product(null, "Thyme", categories.findId("Spices")));

        products.addProduct(new Product(null, "Coffee", categories.findId("Coffee/Tea")));
        products.addProduct(new Product(null, "Coffee Filters", categories.findId("Coffee/Tea")));
        products.addProduct(new Product(null, "Instant Coffee", categories.findId("Coffee/Tea")));
        products.addProduct(new Product(null, "Tea", categories.findId("Coffee/Tea")));
        products.addProduct(new Product(null, "Instant Tea", categories.findId("Coffee/Tea")));

        products.addProduct(new Product(null, "Coke", categories.findId("Beverages")));
        products.addProduct(new Product(null, "Pepsi", categories.findId("Beverages")));
        products.addProduct(new Product(null, "Soda", categories.findId("Beverages")));
        products.addProduct(new Product(null, "Bottled Water", categories.findId("Beverages")));
        products.addProduct(new Product(null, "Beer", categories.findId("Beverages")));
        products.addProduct(new Product(null, "Wine", categories.findId("Beverages")));
        products.addProduct(new Product(null, "Juice", categories.findId("Beverages")));

        products.addProduct(new Product(null, "Aspirin", categories.findId("Medications")));
        products.addProduct(new Product(null, "Advil", categories.findId("Medications")));
        products.addProduct(new Product(null, "Tylenol", categories.findId("Medications")));
        products.addProduct(new Product(null, "Eye Drops", categories.findId("Medications")));
        products.addProduct(new Product(null, "Vitamins", categories.findId("Medications")));

        products.addProduct(new Product(null, "Ketchup", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Mustard", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Mayonnaise", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Salsa", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Soy Sauce", categories.findId("Condiments")));
        products.addProduct(new Product(null, "A1 Steak Sauce", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Hot Sauce", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Cocktail Sauce", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Honey", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Jelly", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Jam", categories.findId("Condiments")));
        products.addProduct(new Product(null, "Peanut Butter", categories.findId("Condiments")));

        // TODO: Add More!

    }
}
