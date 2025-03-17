"use strict";(self.webpackChunkberry_free_material_react_cra=self.webpackChunkberry_free_material_react_cra||[]).push([[2288],{98393:function(e,t,r){var n=r(1582),s=r(20890),i=r(50533),l=r(80184);t.Z=()=>(0,l.jsxs)(n.Z,{direction:"row",justifyContent:"space-between",children:[(0,l.jsx)(s.Z,{variant:"subtitle2",component:i.Z,href:"https://berrydashboard.io",target:"_blank",underline:"hover"}),(0,l.jsx)(s.Z,{variant:"subtitle2",component:i.Z,href:"https://codedthemes.com",target:"_blank",underline:"hover",children:"\xa9"})]})},45290:function(e,t,r){var n=r(64554),s=r(23735),i=r(80184);t.Z=e=>{let{children:t,...r}=e;return(0,i.jsx)(s.Z,{sx:{maxWidth:{xs:400,lg:475},margin:{xs:2.5,md:3},"& > *":{flexGrow:1,flexBasis:"50%"}},content:!1,...r,children:(0,i.jsx)(n.Z,{sx:{p:{xs:2,sm:3,xl:5}},children:t})})}},33914:function(e,t,r){const n=(0,r(66934).ZP)("div")((e=>{let{theme:t}=e;return{backgroundColor:t.palette.primary.light,minHeight:"100vh"}}));t.Z=n},32288:function(e,t,r){r.r(t),r.d(t,{default:function(){return G}});var n=r(43504),s=r(13967),i=r(95193),l=r(61889),a=r(1582),o=r(20890),c=r(94721),d=r(33914),x=r(45290),h=r(11260),m=r(72791),u=r(59434),Z=r(94294),g=r(64554),j=r(48550),p=r(68096),b=r(94925),f=r(77196),y=r(47071),v=r(63466),w=r(13400),P=r(85523),C=r(94454),k=r(81724),S=r(98353);var I=()=>{const e=(0,m.useRef)(!0);return(0,m.useEffect)((()=>()=>{e.current=!1}),[]),e};var R=r.p+"static/media/social-google.9887eb8eb43729cb99f402cfa0de3c44.svg",_=r(52909),E=r(28781);const W=e=>{let t=0;return e.length>5&&(t+=1),e.length>7&&(t+=1),(e=>new RegExp(/[0-9]/).test(e))(e)&&(t+=1),(e=>new RegExp(/[!#@$%^&*)(+=._-]/).test(e))(e)&&(t+=1),(e=>new RegExp(/[a-z]/).test(e)&&new RegExp(/[A-Z]/).test(e))(e)&&(t+=1),t};var z=r(3746),B=r(20165),A=r(80184);var M=e=>{let{...t}=e;const r=(0,s.Z)(),a=I(),d=(0,i.Z)(r.breakpoints.down("md")),x=(0,u.v9)((e=>e.customization)),[h,M]=(0,m.useState)(!1),[D,G]=(0,m.useState)(!0),[q,F]=(0,m.useState)(0),[U,V]=(0,m.useState)(),$=()=>{M(!h)},H=e=>{e.preventDefault()},N=e=>{const t=W(e);var r;F(t),V((r=t)<2?{label:"Poor",color:E.Z.errorMain}:r<3?{label:"Weak",color:E.Z.warningDark}:r<4?{label:"Normal",color:E.Z.orangeMain}:r<5?{label:"Good",color:E.Z.successMain}:r<6?{label:"Strong",color:E.Z.successDark}:{label:"Poor",color:E.Z.errorMain})};return(0,m.useEffect)((()=>{N("123456")}),[]),(0,A.jsxs)(A.Fragment,{children:[(0,A.jsxs)(l.ZP,{container:!0,direction:"column",justifyContent:"center",spacing:2,children:[(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsx)(_.Z,{children:(0,A.jsxs)(Z.Z,{variant:"outlined",fullWidth:!0,onClick:async()=>{console.error("Register")},size:"large",sx:{color:"grey.700",backgroundColor:r.palette.grey[50],borderColor:r.palette.grey[100]},children:[(0,A.jsx)(g.Z,{sx:{mr:{xs:1,sm:2,width:20}},children:(0,A.jsx)("img",{src:R,alt:"google",width:16,height:16,style:{marginRight:d?8:16}})}),"Sign up with Google"]})})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsxs)(g.Z,{sx:{alignItems:"center",display:"flex"},children:[(0,A.jsx)(c.Z,{sx:{flexGrow:1},orientation:"horizontal"}),(0,A.jsx)(Z.Z,{variant:"outlined",sx:{cursor:"unset",m:2,py:.5,px:7,borderColor:`${r.palette.grey[100]} !important`,color:`${r.palette.grey[900]}!important`,fontWeight:500,borderRadius:`${x.borderRadius}px`},disableRipple:!0,disabled:!0,children:"OR"}),(0,A.jsx)(c.Z,{sx:{flexGrow:1},orientation:"horizontal"})]})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,container:!0,alignItems:"center",justifyContent:"center",children:(0,A.jsx)(g.Z,{sx:{mb:2},children:(0,A.jsx)(o.Z,{variant:"subtitle1",children:"Sign up with Email address"})})})]}),(0,A.jsx)(S.J9,{initialValues:{email:"",password:"",submit:null},validationSchema:k.Ry().shape({email:k.Z_().email("Must be a valid email").max(255).required("Email is required"),password:k.Z_().max(255).required("Password is required")}),onSubmit:async(e,t)=>{let{setErrors:r,setStatus:n,setSubmitting:s}=t;try{a.current&&(n({success:!0}),s(!1))}catch(i){console.error(i),a.current&&(n({success:!1}),r({submit:i.message}),s(!1))}},children:e=>{let{errors:s,handleBlur:i,handleChange:a,handleSubmit:c,isSubmitting:x,touched:m,values:u}=e;return(0,A.jsxs)("form",{noValidate:!0,onSubmit:c,...t,children:[(0,A.jsxs)(l.ZP,{container:!0,spacing:d?0:2,children:[(0,A.jsx)(l.ZP,{item:!0,xs:12,sm:6,children:(0,A.jsx)(j.Z,{fullWidth:!0,label:"First Name",margin:"normal",name:"fname",type:"text",defaultValue:"",sx:{...r.typography.customInput}})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,sm:6,children:(0,A.jsx)(j.Z,{fullWidth:!0,label:"Last Name",margin:"normal",name:"lname",type:"text",defaultValue:"",sx:{...r.typography.customInput}})})]}),(0,A.jsxs)(p.Z,{fullWidth:!0,error:Boolean(m.email&&s.email),sx:{...r.typography.customInput},children:[(0,A.jsx)(b.Z,{htmlFor:"outlined-adornment-email-register",children:"Email Address / Username"}),(0,A.jsx)(f.Z,{id:"outlined-adornment-email-register",type:"email",value:u.email,name:"email",onBlur:i,onChange:a,inputProps:{}}),m.email&&s.email&&(0,A.jsx)(y.Z,{error:!0,id:"standard-weight-helper-text--register",children:s.email})]}),(0,A.jsxs)(p.Z,{fullWidth:!0,error:Boolean(m.password&&s.password),sx:{...r.typography.customInput},children:[(0,A.jsx)(b.Z,{htmlFor:"outlined-adornment-password-register",children:"Password"}),(0,A.jsx)(f.Z,{id:"outlined-adornment-password-register",type:h?"text":"password",value:u.password,name:"password",label:"Password",onBlur:i,onChange:e=>{a(e),N(e.target.value)},endAdornment:(0,A.jsx)(v.Z,{position:"end",children:(0,A.jsx)(w.Z,{"aria-label":"toggle password visibility",onClick:$,onMouseDown:H,edge:"end",size:"large",children:h?(0,A.jsx)(z.Z,{}):(0,A.jsx)(B.Z,{})})}),inputProps:{}}),m.password&&s.password&&(0,A.jsx)(y.Z,{error:!0,id:"standard-weight-helper-text-password-register",children:s.password})]}),0!==q&&(0,A.jsx)(p.Z,{fullWidth:!0,children:(0,A.jsx)(g.Z,{sx:{mb:2},children:(0,A.jsxs)(l.ZP,{container:!0,spacing:2,alignItems:"center",children:[(0,A.jsx)(l.ZP,{item:!0,children:(0,A.jsx)(g.Z,{style:{backgroundColor:null===U||void 0===U?void 0:U.color},sx:{width:85,height:8,borderRadius:"7px"}})}),(0,A.jsx)(l.ZP,{item:!0,children:(0,A.jsx)(o.Z,{variant:"subtitle1",fontSize:"0.75rem",children:null===U||void 0===U?void 0:U.label})})]})})}),(0,A.jsx)(l.ZP,{container:!0,alignItems:"center",justifyContent:"space-between",children:(0,A.jsx)(l.ZP,{item:!0,children:(0,A.jsx)(P.Z,{control:(0,A.jsx)(C.Z,{checked:D,onChange:e=>G(e.target.checked),name:"checked",color:"primary"}),label:(0,A.jsxs)(o.Z,{variant:"subtitle1",children:["Agree with \xa0",(0,A.jsx)(o.Z,{variant:"subtitle1",component:n.rU,to:"#",children:"Terms & Condition."})]})})})}),s.submit&&(0,A.jsx)(g.Z,{sx:{mt:3},children:(0,A.jsx)(y.Z,{error:!0,children:s.submit})}),(0,A.jsx)(g.Z,{sx:{mt:2},children:(0,A.jsx)(_.Z,{children:(0,A.jsx)(Z.Z,{disableElevation:!0,disabled:x,fullWidth:!0,size:"large",type:"submit",variant:"contained",color:"secondary",children:"Sign up"})})})]})}})]})},D=r(98393);var G=()=>{const e=(0,s.Z)(),t=(0,i.Z)(e.breakpoints.down("md"));return(0,A.jsx)(d.Z,{children:(0,A.jsxs)(l.ZP,{container:!0,direction:"column",justifyContent:"flex-end",sx:{minHeight:"100vh"},children:[(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsx)(l.ZP,{container:!0,justifyContent:"center",alignItems:"center",sx:{minHeight:"calc(100vh - 68px)"},children:(0,A.jsx)(l.ZP,{item:!0,sx:{m:{xs:1,sm:3},mb:0},children:(0,A.jsx)(x.Z,{children:(0,A.jsxs)(l.ZP,{container:!0,spacing:2,alignItems:"center",justifyContent:"center",children:[(0,A.jsx)(l.ZP,{item:!0,sx:{mb:3},children:(0,A.jsx)(n.rU,{to:"#",children:(0,A.jsx)(h.Z,{})})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsx)(l.ZP,{container:!0,direction:t?"column-reverse":"row",alignItems:"center",justifyContent:"center",children:(0,A.jsx)(l.ZP,{item:!0,children:(0,A.jsxs)(a.Z,{alignItems:"center",justifyContent:"center",spacing:1,children:[(0,A.jsx)(o.Z,{color:e.palette.secondary.main,gutterBottom:!0,variant:t?"h3":"h2",children:"Sign up"}),(0,A.jsx)(o.Z,{variant:"caption",fontSize:"16px",textAlign:t?"center":"inherit",children:"Enter your credentials to continue"})]})})})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsx)(M,{})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsx)(c.Z,{})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,children:(0,A.jsx)(l.ZP,{item:!0,container:!0,direction:"column",alignItems:"center",xs:12,children:(0,A.jsx)(o.Z,{component:n.rU,to:"/pages/login/login3",variant:"subtitle1",sx:{textDecoration:"none"},children:"Already have an account?"})})})]})})})})}),(0,A.jsx)(l.ZP,{item:!0,xs:12,sx:{m:3,mt:1},children:(0,A.jsx)(D.Z,{})})]})})}}}]);
//# sourceMappingURL=2288.97007449.chunk.js.map