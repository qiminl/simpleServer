/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;

import java.util.ArrayList;

/**
 *
 * @author liuqi
 */
class Campaign {
    private String name;
    private double price;
    private target[] target;
    
    public void setName (String name ){this.name = name;}
    public void setPrice (double price){this.price = price;}
    public void setTarget (target[] target){this.target = target;}
    
    public String getName (){return this.name;}
    public double getPrice (){return this.price;}
    public target[] getTarget (){return this.target;}
}
