"use strict";(self.webpackChunkcar_wash_management=self.webpackChunkcar_wash_management||[]).push([[5418],{98393:function(e,n,r){var t=r(1582),o=r(20890),i=r(50533),a=r(80184);n.Z=()=>(0,a.jsxs)(t.Z,{direction:"row",justifyContent:"space-between",children:[(0,a.jsx)(o.Z,{variant:"subtitle2",component:i.Z,href:"http://localhost:3000",target:"_blank",underline:"hover"}),(0,a.jsx)(o.Z,{variant:"subtitle2",component:i.Z,href:"http://localhost:3000",target:"_blank",underline:"hover",children:"\xa9"})]})},45290:function(e,n,r){var t=r(64554),o=r(23735),i=r(80184);n.Z=e=>{let{children:n,...r}=e;return(0,i.jsx)(o.Z,{sx:{maxWidth:{xs:400,lg:475},margin:{xs:2.5,md:3},"& > *":{flexGrow:1,flexBasis:"50%"}},content:!1,...r,children:(0,i.jsx)(t.Z,{sx:{p:{xs:2,sm:3,xl:5}},children:n})})}},33914:function(e,n,r){const t=(0,r(66934).ZP)("div")((e=>{let{theme:n}=e;return{backgroundColor:n.palette.primary.light,minHeight:"100vh"}}));n.Z=t},25418:function(e,n,r){r.r(n),r.d(n,{default:function(){return w}});var t=r(43504),o=r(13967),i=r(95193),a=r(61889),s=r(1582),l=r(20890),c=r(94721),u=r(33914),d=r(45290),x=r(72791),h=r(16871),m=r(19035),Z=r(48550),p=r(94294),j=r(23735),g=r(91923),v=r(80184);var f=()=>{const[e,n]=(0,x.useState)(""),[r,t]=(0,x.useState)(""),o=(0,h.s0)();return(0,v.jsx)("div",{children:(0,v.jsx)(j.Z,{children:(0,v.jsxs)(a.ZP,{container:!0,direction:"row",spacing:g.dv,children:[(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(Z.Z,{label:"Username",required:!0,fullWidth:!0,variant:"outlined",value:e||"",onChange:e=>n(e.target.value)})}),(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(Z.Z,{label:"Password",required:!0,fullWidth:!0,variant:"outlined",type:"password",value:r||"",onChange:e=>t(e.target.value)})}),(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(p.Z,{variant:"contained",color:"error",onClick:async()=>{try{const n={username:e,password:r},t=await(0,m.j0)("/auth/login",n),{token:i,username:a,roles:s}=t;localStorage.setItem("token",i),localStorage.setItem("username",a),localStorage.setItem("roles",JSON.stringify(s)),s.includes("JOBCARD")?o("/card/table",{replace:!0}):s.includes("SPARES")?o("/spares/table",{replace:!0}):o("/card/table",{replace:!0})}catch(n){console.error("Login failed",n),alert("Login failed. Please check your credentials and try again.")}},children:"Login"})})]})})})},b=r(11260),y=r(98393);var w=()=>{const e=(0,o.Z)(),n=(0,i.Z)(e.breakpoints.down("md"));return(0,v.jsx)(u.Z,{children:(0,v.jsxs)(a.ZP,{container:!0,direction:"column",justifyContent:"flex-end",sx:{minHeight:"100vh"},children:[(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(a.ZP,{container:!0,justifyContent:"center",alignItems:"center",sx:{minHeight:"calc(100vh - 68px)"},children:(0,v.jsx)(a.ZP,{item:!0,sx:{m:{xs:1,sm:3},mb:0},children:(0,v.jsx)(d.Z,{children:(0,v.jsxs)(a.ZP,{container:!0,spacing:2,alignItems:"center",justifyContent:"center",children:[(0,v.jsx)(a.ZP,{item:!0,sx:{mb:3},children:(0,v.jsx)(t.rU,{to:"#",children:(0,v.jsx)(b.Z,{})})}),(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(a.ZP,{container:!0,direction:n?"column-reverse":"row",alignItems:"center",justifyContent:"center",children:(0,v.jsx)(a.ZP,{item:!0,children:(0,v.jsxs)(s.Z,{alignItems:"center",justifyContent:"center",spacing:1,children:[(0,v.jsx)(l.Z,{color:e.palette.secondary.main,gutterBottom:!0,variant:n?"h3":"h2",children:"Hi, Welcome Back"}),(0,v.jsx)(l.Z,{variant:"caption",fontSize:"16px",textAlign:n?"center":"inherit",children:"Enter your credentials to continue"})]})})})}),(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(f,{})}),(0,v.jsx)(a.ZP,{item:!0,xs:12,children:(0,v.jsx)(c.Z,{})})]})})})})}),(0,v.jsx)(a.ZP,{item:!0,xs:12,sx:{m:3,mt:1},children:(0,v.jsx)(y.Z,{})})]})})}},50533:function(e,n,r){r.d(n,{Z:function(){return k}});var t=r(63366),o=r(87462),i=r(72791),a=r(63733),s=r(94419),l=r(14036),c=r(66934),u=r(31402),d=r(68221),x=r(42071),h=r(20890),m=r(75878),Z=r(21217);function p(e){return(0,Z.Z)("MuiLink",e)}var j=(0,m.Z)("MuiLink",["root","underlineNone","underlineHover","underlineAlways","button","focusVisible"]),g=r(18529),v=r(12065);const f={primary:"primary.main",textPrimary:"text.primary",secondary:"secondary.main",textSecondary:"text.secondary",error:"error.main"};var b=e=>{let{theme:n,ownerState:r}=e;const t=(e=>f[e]||e)(r.color),o=(0,g.DW)(n,`palette.${t}`,!1)||r.color,i=(0,g.DW)(n,`palette.${t}Channel`);return"vars"in n&&i?`rgba(${i} / 0.4)`:(0,v.Fq)(o,.4)},y=r(80184);const w=["className","color","component","onBlur","onFocus","TypographyClasses","underline","variant","sx"],P=(0,c.ZP)(h.Z,{name:"MuiLink",slot:"Root",overridesResolver:(e,n)=>{const{ownerState:r}=e;return[n.root,n[`underline${(0,l.Z)(r.underline)}`],"button"===r.component&&n.button]}})((e=>{let{theme:n,ownerState:r}=e;return(0,o.Z)({},"none"===r.underline&&{textDecoration:"none"},"hover"===r.underline&&{textDecoration:"none","&:hover":{textDecoration:"underline"}},"always"===r.underline&&(0,o.Z)({textDecoration:"underline"},"inherit"!==r.color&&{textDecorationColor:b({theme:n,ownerState:r})},{"&:hover":{textDecorationColor:"inherit"}}),"button"===r.component&&{position:"relative",WebkitTapHighlightColor:"transparent",backgroundColor:"transparent",outline:0,border:0,margin:0,borderRadius:0,padding:0,cursor:"pointer",userSelect:"none",verticalAlign:"middle",MozAppearance:"none",WebkitAppearance:"none","&::-moz-focus-inner":{borderStyle:"none"},[`&.${j.focusVisible}`]:{outline:"auto"}})}));var k=i.forwardRef((function(e,n){const r=(0,u.Z)({props:e,name:"MuiLink"}),{className:c,color:h="primary",component:m="a",onBlur:Z,onFocus:j,TypographyClasses:g,underline:v="always",variant:b="inherit",sx:k}=r,C=(0,t.Z)(r,w),{isFocusVisibleRef:S,onBlur:A,onFocus:D,ref:B}=(0,d.Z)(),[W,I]=i.useState(!1),L=(0,x.Z)(n,B),R=(0,o.Z)({},r,{color:h,component:m,focusVisible:W,underline:v,variant:b}),F=(e=>{const{classes:n,component:r,focusVisible:t,underline:o}=e,i={root:["root",`underline${(0,l.Z)(o)}`,"button"===r&&"button",t&&"focusVisible"]};return(0,s.Z)(i,p,n)})(R);return(0,y.jsx)(P,(0,o.Z)({color:h,className:(0,a.Z)(F.root,c),classes:g,component:m,onBlur:e=>{A(e),!1===S.current&&I(!1),Z&&Z(e)},onFocus:e=>{D(e),!0===S.current&&I(!0),j&&j(e)},ref:L,ownerState:R,variant:b,sx:[...Object.keys(f).includes(h)?[]:[{color:h}],...Array.isArray(k)?k:[k]]},C))}))}}]);
//# sourceMappingURL=5418.434471f8.chunk.js.map