package main.java.org.runaway.constructors;

import java.util.Date;

public class Price {

    private String formated_price;
    private double discount;
    private double initial_price;
    private double final_price;
    private boolean sale;
    private boolean preorder;
    private Date last_update;

    public Price(boolean preorder, String formated_price, double discount, double initial_price, double final_price, Date last_update) {
        this.formated_price = formated_price;
        this.discount = discount;
        this.initial_price = initial_price;
        this.final_price = final_price;
        this.sale = discount > 0;
        this.preorder = preorder;
        this.last_update = last_update;
    }

    public String getFormated_price() {
        return formated_price;
    }

    public double getInitial_price() {
        return initial_price;
    }

    public double getFinal_price() {
        return final_price;
    }

    public double getDiscount() {
        return discount;
    }

    public boolean isSale() {
        return sale;
    }

    public boolean isPreorder() {
        return preorder;
    }

    public Date getLastUpdate() {
        return last_update;
    }
}
