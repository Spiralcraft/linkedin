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

import spiralcraft.common.ContextualException;
import spiralcraft.common.callable.BinaryFunction;
import spiralcraft.data.DataComposite;
import spiralcraft.json.FromJson;
import spiralcraft.lang.Binding;
import spiralcraft.lang.Focus;
import spiralcraft.lang.parser.Struct;
import spiralcraft.lang.reflect.BeanReflector;
import spiralcraft.lang.spi.ThreadLocalChannel;
import spiralcraft.text.ParseException;
import spiralcraft.vfs.util.ByteArrayResource;

public class Call<Tresult>
  extends spiralcraft.oauth2.Call<Tresult>
{
  private Binding<Tresult> resultX;
  private Binding<Struct> jsonStruct;
  private ThreadLocalChannel<Struct> jsonChannel;
  private FromJson<Struct,byte[]> fromJson;
  private BinaryFunction<byte[],Struct,Struct,ParseException> fromJsonFn;
  private boolean ignoreUnrecognizedFields=false;
//  private DataReader resultReader;
  
  { 
    clientReflector=BeanReflector.getInstance(Client.class);
  }
  
  public void setIgnoreUnrecognizedFields(boolean ignoreUnrecognizedFields)
  { this.ignoreUnrecognizedFields=ignoreUnrecognizedFields;
  }
  
  public void setJSONStruct(Binding<Struct> jsonStruct)
  { this.jsonStruct=jsonStruct;
  }
  
  /**
   * The expression that maps the deserialized JSON result into the app specific
   *   return type of this call.
   *
   * @param resultX
   */
  public void setResultX(Binding<Tresult> resultX)
  { this.resultX=resultX;
  }
  
  @Override
  protected Operation resolveOperation(OperationType type)
  {
    switch (type)
    {
      case GET:
        return new LIReadOperation();
      case PUT:
      case DELETE:
    }
    return super.resolveOperation(type);

  }  
  
  @Override
  protected Focus<?> bindExports(Focus<?> chain)
    throws ContextualException
  {
    chain=super.bindExports(chain);
    if (jsonStruct==null)
    { throw new ContextualException("Property 'JSONStruct' required",getDeclarationInfo());
    }
    jsonStruct.bind(chain);
    fromJson=new FromJson<Struct,byte[]>(jsonStruct.get());
    fromJson.setIgnoreUnrecognizedFields(ignoreUnrecognizedFields);
    fromJsonFn=fromJson.getBinaryFn();
    jsonChannel=new ThreadLocalChannel<Struct>(jsonStruct.getReflector());
    chain=chain.chain(jsonChannel);
    if (resultX==null)
    { throw new ContextualException("Property 'resultX' required",getDeclarationInfo());
    }
    resultX.bind(chain);
    
    // OBSOLETE - READ NEW MODEL IN JSON
//    resultReader=new DataReader();
//    
//    FrameHandler handler
//      =XmlBean.<FrameHandler>instantiate
//        (URI.create
//          ("class:/spiralcraft/linkedin/api/"
//          +URIUtil.unencodedLocalName(resultType.getURI())
//          +".handler.xml"
//          )
//        ).get();
//    
//    RootFrame<Tresult> rootHandler
//      =new RootFrame<Tresult>();
//    rootHandler.setChildren(new FrameHandler[] {handler});
//    rootHandler.setCaptureChildObject(true);
//    rootHandler.bind();
////    rootHandler.setDebug(true);
//    resultReader.setFrameHandler(rootHandler);
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
        ByteArrayResource bytes=ByteArrayResource.copyOf(in);
        Struct struct=fromJsonFn.evaluate(bytes.getBackingStore(),jsonStruct.get());
        try
        { 
          jsonChannel.push(struct);
          return resultX.get();
        }
        finally
        { jsonChannel.pop();
        }
      }
      catch (ParseException x)
      { throw new IOException("Error reading result",x);
      }
      finally
      { }

//   // XXX Use JSON
//      try
//      {
//        
//        Tresult result
//          =(Tresult) resultReader.readFromInputStream
//            (in,((DataReflector<Tresult>) resultReflector).getType(),uri);
//        
//        return result;
//      }
//      catch (DataException x)
//      { throw new IOException("Error reading result",x);
//      }
//      catch (SAXException x)
//      { throw new IOException("Error reading result",x);
//      }
    }
    
  }  
}
