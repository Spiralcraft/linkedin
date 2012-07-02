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

import java.io.IOException;

import spiralcraft.util.URIUtil;
import spiralcraft.vfs.url.URLMessage;

public class Session
  extends spiralcraft.oauth1.Session
{

  public Session(Client client)
  { super(client);
  }
  
  @Override
  protected void postAuthenticate()
    throws IOException
  { 
    URLMessage result
      =call("GET",URIUtil.addPathSegment(client.getApiURI(),"people/~"),null);
    log.fine(result.toString());
  }
  
}
