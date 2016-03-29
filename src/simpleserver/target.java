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
public class target {
    private String name; //target : attr_A
    private String[] attr_list;
    
    public void setName(String name){this.name = name;}
    public void setAttr_list(String[] attr_list){this.attr_list = attr_list;}
    public String getName( ){return this.name;}
    public String[] getAttr_list( ){return this.attr_list;}
}
