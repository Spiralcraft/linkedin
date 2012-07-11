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
import java.net.URI;

import org.xml.sax.SAXException;

import spiralcraft.common.ContextualException;
import spiralcraft.data.DataComposite;
import spiralcraft.data.DataException;
import spiralcraft.data.Type;
import spiralcraft.data.lang.DataReflector;
import spiralcraft.data.persist.XmlBean;
import spiralcraft.data.sax.DataReader;
import spiralcraft.data.sax.FrameHandler;
import spiralcraft.data.sax.RootFrame;
import spiralcraft.lang.Focus;
import spiralcraft.lang.reflect.BeanReflector;
import spiralcraft.util.URIUtil;

public class Call<Tresult extends DataComposite>
  extends spiralcraft.oauth1.Call<Tresult>
{
  private Type<Tresult> resultType;
  private DataReader resultReader;
  
  { 
    clientReflector=BeanReflector.getInstance(Client.class);
  }
  
  
  @Override
  protected Operation resolveOperation(OperationType type)
  {
    switch (type)
    {
      case GET:
        return new LIReadOperation();
    }
    return super.resolveOperation(type);

  }  
  
  @Override
  protected Focus<?> bindExports(Focus<?> chain)
    throws ContextualException
  {
    chain=super.bindExports(chain);
    resultType=((DataReflector<Tresult>) resultReflector).getType();
    resultReader=new DataReader();
    
    FrameHandler handler
      =XmlBean.<FrameHandler>instantiate
        (URI.create
          ("class:/spiralcraft/linkedin/api/"
          +URIUtil.unencodedLocalName(resultType.getURI())
          +".handler.xml"
          )
        ).get();
    
    RootFrame<Tresult> rootHandler
      =new RootFrame<Tresult>();
    rootHandler.setChildren(new FrameHandler[] {handler});
    rootHandler.setCaptureChildObject(true);
    rootHandler.bind();
//    rootHandler.setDebug(true);
    resultReader.setFrameHandler(rootHandler);
    return chain;
  }
  
  class LIReadOperation
    extends OAuthReadOperation
  {
    @SuppressWarnings("unchecked")
    @Override
    protected Tresult readStream(InputStream in,URI uri)
      throws IOException
    {      
      try
      {
        Tresult result
          =(Tresult) resultReader.readFromInputStream
            (in,((DataReflector<Tresult>) resultReflector).getType(),uri);
        
        return result;
      }
      catch (DataException x)
      { throw new IOException("Error reading result",x);
      }
      catch (SAXException x)
      { throw new IOException("Error reading result",x);
      }
    }
    
  }  
}
