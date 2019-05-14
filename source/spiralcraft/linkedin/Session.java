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
import java.io.InputStream;

import org.xml.sax.SAXException;

import spiralcraft.json.FromJson;
import spiralcraft.lang.Channel;
import spiralcraft.lang.Expression;
import spiralcraft.lang.parser.Struct;
import spiralcraft.util.URIUtil;
import spiralcraft.vfs.util.ByteArrayResource;

public class Session
  extends spiralcraft.oauth2.Session
{
  @SuppressWarnings("unchecked")
  private static final 
     Channel<Struct> idStruct = (Channel) Expression.bindStatic("{ id:=\"\" }");
  
  public Session(Client client)
  { super(client);
  }
  
  @Override
  protected void postAuthenticate()
    throws IOException
  { 
    log.fine("Calling "+URIUtil.addPathSegment(client.getApiURI(),"me"));
    InputStream result
      =call("GET",URIUtil.addPathSegment(client.getApiURI(),"me"),null);
 
    if (true || logLevel.isFine())
    { log.fine("Got result from ID call");
    }
    try
    {
      ByteArrayResource resource=ByteArrayResource.copyOf(result);
      log.fine(resource.asString());
      FromJson<Struct,String> fromJson=new FromJson<Struct,String>(idStruct.get());
      fromJson.setIgnoreUnrecognizedFields(true);
      Struct idStruct=fromJson.getStringFn().evaluate(resource.asString(),null);
      log.fine(idStruct.toString());
      this.oauthId=(String) idStruct.getValue("id");
      log.fine("Got id: "+oauthId);
      /*
      <!-- 
      {"localizedLastName":"Toth"
      ,"lastName":{"localized":{"en_US":"Toth"},"preferredLocale":{"country":"US","language":"en"}}
      ,"firstName":{"localized":{"en_US":"Michael"},"preferredLocale":{"country":"US","language":"en"}}
      ,"profilePicture":{"displayImage":"urn:li:digitalmediaAsset:C4E03AQGlrnUjcGhPvA"}
      ,"id":"31qa2B4abO"
      ,"localizedFirstName":"Michael"
      }
      -->
      */
//      ParseTree resultTree
//        =ParseTreeFactory.fromInputStream(result);
//      
//      if (logLevel.isFine())
//      { log.fine(""+resultTree.getDocument().getRootElement());
//      }
//      this.oauthId
//        =resultTree.getDocument()
//          .getRootElement()
//          .getChildByQName("id")
//          .getCharacters();
      
      
    }
    catch (Exception x)
    { throw new IOException("Error reading response",x);
    }
        
  }
  
}
