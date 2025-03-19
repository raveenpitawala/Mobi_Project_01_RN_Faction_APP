//Group No : Group 7
//Index Numbers : AS2021934 , AS2021905

package com.example.cloth;

import static org.apache.commons.lang3.StringUtils.capitalize;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.cardview.widget.CardView;
import androidx.room.Room;

import com.example.cloth.data.AppDatabase;
import com.example.cloth.data.Item;
import com.example.cloth.data.ItemDao;

public class MainActivity2 extends AppCompatActivity {

    private ItemDao itemDao;

    private final Map<String, Integer> activity = new HashMap<String, Integer>() {{
        put("women", R.layout.activity_submenu_women);
        put("men", R.layout.activity_submenu_men);
        put("kids", R.layout.activity_submenu_kids);
        put("home", R.layout.activity_home);
        put("/", R.layout.activity_items_view);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "clothes_shop.db").allowMainThreadQueries().build();
        itemDao = db.itemDao();

        try {
            if (itemDao.getItemCount() != 0) {
                itemDao.deleteAllItems();
            }
            List<Item> items = readItemsFromCSV("item.csv"); // Adjust the path accordingly
            insertItemsIntoDatabase(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private List<Item> readItemsFromCSV(String csvFilePath) throws IOException {
        List<Item> items = new ArrayList<>();

        InputStream inputStream = getAssets().open(csvFilePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] values = line.split(",");
                double price = Double.parseDouble(values[4]);
                int stockCount = Integer.parseInt(values[6]);
                Item item = new Item();
                item.name = values[0];
                item.category = values[1];
                item.subCategory = values[2];
                item.price = price;
                item.availableColors = values[5];
                item.stockCount = stockCount;
                item.image = values[3];
                items.add(item);
            } catch (Exception ignored) {

            }
        }
        reader.close();
        return items;
    }

    private void insertItemsIntoDatabase(List<Item> items) {
        new Thread(() -> {
            for (Item item : items) {
                itemDao.insert(item);
            }
        }).start();
    }

    public void nextPage(View view) {
        String tag = (String) view.getTag();
        if (tag.contains("/")) {
            String[] parts = tag.split("/");
            String category = parts[0];
            String subcategory = parts[1];

            Integer layoutId = activity.get("/");
            if (layoutId != null) {
                setUpItemPage(layoutId,category,subcategory);
            } else {
                Toast.makeText(this, "Clicked button with tag: " + tag, Toast.LENGTH_SHORT).show();
            }
        } else {
            Integer layoutId = activity.get(tag);
            if (layoutId != null) {
                setContentView(layoutId);
            } else {
                Toast.makeText(this, "Clicked button with tag: " + tag, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpItemPage(Integer layoutId, String category, String subcategory) {
        setContentView(layoutId);
        TextView categoryView = findViewById(R.id.categoryTextView);
        TextView subcategoryView = findViewById(R.id.subcategoryTextView);
        ImageView back_btn = findViewById(R.id.button_item_view_back);
        LinearLayout item_container = findViewById(R.id.itemContainer);

        categoryView.setText(category.toUpperCase());
        subcategoryView.setText(subcategory.toUpperCase());
        back_btn.setTag(category);

        List<Item> items = itemDao.getItemsByCategoryAndSubcategory(capitalize(category), capitalize(subcategory));
        if (!items.isEmpty()) {
            item_container.removeAllViews();
            for (Item item : items) {
                createItemCardView(item,item_container);
            }
        } else {
            Toast.makeText(this, "list length" + items.size(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void createItemCardView(Item item, LinearLayout item_container) {
        View itemView = getLayoutInflater().inflate(R.layout.activity_item, null);
        TextView productNameTextView = itemView.findViewById(R.id.product_name);
        TextView priceTextView = itemView.findViewById(R.id.product_price);
        LinearLayout colorContainer = itemView.findViewById(R.id.color_option_container);
        productNameTextView.setText(item.name);
        priceTextView.setText(priceTextView.getText()+Double.toString(item.price));
        ImageView imageView = itemView.findViewById(R.id.product_image);
        try {
            InputStream inputStream = getAssets().open(item.image);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            inputStream.close(); // Always close the stream after usage
        } catch (IOException ignored) {
        }

        for (String color_opt : item.availableColors.split("/")) {
            View colorView = getLayoutInflater().inflate(R.layout.color_option, null);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) CardView color_option = colorView.findViewById(R.id.color_view);
            color_option.setCardBackgroundColor(Color.parseColor(color_opt));
            colorContainer.addView(colorView);
        }
        item_container.addView(itemView);
    }

    public static String getHexFromColorName(String colorName) {
        int color = Color.parseColor(colorName); // Convert color name to Color int
        return String.format("#%06X", (0xFFFFFF & color)); // Format it as hex
    }
}