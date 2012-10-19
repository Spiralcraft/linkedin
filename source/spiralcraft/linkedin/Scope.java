package spiralcraft.linkedin;

public enum Scope
{
  BASIC_PROFILE("r_basicprofile")
  ,FULL_PROFILE("r_fullprofile")
  ,EMAIL_ADDRESS("r_emailaddress")
  ,NETWORK("r_network")
  ,CONTACT_INFO("r_contactinfo")
  ,NETWORK_UPDATES("rw_nus")
  ,GROUP_DISCUSSIONS("rw_groups")
  ,MESSAGES("w_messages")
  ;

  private final String protocolName;
  
  private Scope(String protocolName)
  { this.protocolName=protocolName;
  }
  
  public String protocolName()
  { return protocolName;
  }
}
