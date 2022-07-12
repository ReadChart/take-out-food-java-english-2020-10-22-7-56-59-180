import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("============= Order details =============\n");
        List<Item> itemList = itemRepository.findAll();
        Map<Item, Integer> cart = new HashMap<>();
        List<Item> discount = new ArrayList<>();
        List<SalesPromotion> salesDiscountItems = salesPromotionRepository.findAll();
        double total = 0.0;
        double totalType1 = 0.0;
        double totalType2 = 0.0;
        StringBuilder fifOffMessage = new StringBuilder("Half price for certain dishes (");

        for (String input: inputs) {
            String[] inputSplit = input.split("x");
            if(inputSplit.length < 2) {
                return "wrong input, please check";
            }
            String itemId = inputSplit[0].trim();
            int amount=Integer.parseInt(inputSplit[1].trim());
            Optional<Item> optionalItem = itemList.stream().filter(item -> item.getId().equals(itemId)).findFirst();
            optionalItem.ifPresent(
                    item -> {
                        stringBuilder.append(item.getName())
                                .append(" x ")
                                .append(inputSplit[1].trim())
                                .append(" = ")
                                .append(((int) (item.getPrice() * amount)))
                                .append(" yuan\n");
                        cart.put(item, amount);
                    }
            );
        }
        stringBuilder.append("-----------------------------------\n");

        for (Map.Entry<Item, Integer> entry : cart.entrySet()) {
            System.out.println(entry);
            Item k = entry.getKey();
            Integer v = entry.getValue();
            total += k.getPrice()  * v * 1.0;
            if (salesDiscountItems.get(1).getRelatedItems().contains(k.getId())) {
                totalType2 += (k.getPrice() * 0.5 * v);
                discount.add(k);
            }else{
                totalType2 += k.getPrice() * v;
            }
        }

        if (total >= 30.0) {
            totalType1 = total - 6.0;
        }else{
            totalType1 = total;
        }


        if (totalType1 <= totalType2  && total >= 30) {
            stringBuilder.append("Promotion used:\n")
            .append("满30减6 yuan，saving 6 yuan\n")
            .append("-----------------------------------\n");
            total = totalType1;
        }else if (total >= 30){
            StringJoiner stringJoiner = new StringJoiner("，");
            discount.sort(Comparator.comparing(Item::getName));
            discount.forEach(item -> stringJoiner.add(item.getName()));
            stringBuilder.append("Promotion used:\n")
            .append(fifOffMessage)
            .append(stringJoiner.toString())
            .append(")，saving " + (int)(total - totalType2) + " yuan\n")
            .append("-----------------------------------\n");
            total = totalType2;
        }
        stringBuilder.append("Total：" + (int)total + " yuan\n")
        .append("===================================");
        System.out.println(stringBuilder);

        return stringBuilder.toString();
    }
}
