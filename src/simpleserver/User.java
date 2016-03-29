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
public class User {
    private String name;    
    private int[] profile;
    
    public void setName (String name ){this.name = name;}
    public void setProfile (int[] profile){this.profile = profile;}
    
    public String getName (){return this.name;}
    public int[] getTarget (){return this.profile;}
    
}
