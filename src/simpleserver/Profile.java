/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;

/**
 *
 * @author liuqi
 */
public class Profile {
    private String attr;
    
    //public void setName(String name){this.name = name;}
    public void setAttr(String attr_list){this.attr = attr_list;}
    //public String getName( ){return this.name;}
    public String getAttr( ){return this.attr;}
}
