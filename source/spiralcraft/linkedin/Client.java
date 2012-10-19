//
//Copyright (c) 2012 Michael Toth
//Spiralcraft Inc., All Rights Reserved
//
//This package is part of the Spiralcraft project and is licensed under
//a multiple-license framework.
//
//You may not use this file except in compliance with the terms found in the
//SPIRALCRAFT-LICENSE.txt file at the top of this distribution, or available
//at http://www.spiralcraft.org/licensing/SPIRALCRAFT-LICENSE.txt.
//
//Unless otherwise agreed to in writing, this software is distributed on an
//"AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
//
package spiralcraft.linkedin;

import java.net.URI;

import spiralcraft.lang.reflect.BeanReflector;
import spiralcraft.util.URIUtil;

public class Client
  extends spiralcraft.oauth1.Client
{ 

  private URI credentialRequestURIBase;
  private Scope[] scopes;
  
  { sessionReflector=BeanReflector.getInstance(Session.class);
  }
  
  @Override
  public Session newSession()
  { return new Session(this);
  }
  
  public void setCredentialRequestURIBase(URI credentialRequestURIBase)
  { this.credentialRequestURIBase=credentialRequestURIBase;
  }
  
  public void setScopes(Scope[] scopes)
  { this.scopes=scopes;
  }
  
  @Override
  public URI getCredentialRequestURI()
  { 
    if (credentialRequestURIBase==null)
    { credentialRequestURIBase=super.getCredentialRequestURI();
    }
    if (credentialRequestURIBase!=null)
    {
      if (scopes==null)
      { return credentialRequestURIBase;
      }
      else
      {
        StringBuilder scopeString=new StringBuilder();
        scopeString.append("scope=");
        boolean first=true;
        for (Scope scope:scopes)
        { 
          if (first)
          { first=false;
          }
          else
          { scopeString.append("+");
          }
          scopeString.append(scope.protocolName());
        }
        String query=credentialRequestURIBase.getRawQuery();
        if (query!=null)
        { 
          return URIUtil.replaceRawQuery
            (credentialRequestURIBase
            ,query+"&"+scopeString
            );
        } 
        else
        { 
          return URIUtil.replaceRawQuery
            (credentialRequestURIBase
            ,scopeString.toString()
            );
        }
      }
    }
    else
    { return null;
    }
  }
}
 